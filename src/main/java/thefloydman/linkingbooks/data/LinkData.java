package thefloydman.linkingbooks.data;

import com.jcraft.jorbis.Block;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.linking.LinkEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record LinkData(ResourceLocation dimension, BlockPos blockPos, float rotation, UUID uuid,
                       List<ResourceLocation> linkEffects) {

    public static final Codec<LinkData> CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            ResourceLocation.CODEC.fieldOf("dimension").forGetter(LinkData::dimension),
                            BlockPos.CODEC.fieldOf("blockpos").forGetter(LinkData::blockPos),
                            Codec.FLOAT.fieldOf("rotation").forGetter(LinkData::rotation),
                            UUIDUtil.CODEC.fieldOf("uuid").forGetter(LinkData::uuid),
                            Codec.list(ResourceLocation.CODEC).fieldOf("linkeffects").forGetter(LinkData::linkEffects)
                    )
                    .apply(codecBuilderInstance, LinkData::new)
    );

    public static final StreamCodec<ByteBuf, LinkData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, LinkData::dimension,
            BlockPos.STREAM_CODEC, LinkData::blockPos,
            ByteBufCodecs.FLOAT, LinkData::rotation,
            UUIDUtil.STREAM_CODEC, LinkData::uuid,
            ByteBufCodecs.fromCodec(Codec.list(ResourceLocation.CODEC)), LinkData::linkEffects,
            LinkData::new
    );

    public static final LinkData EMPTY = new LinkData(ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"), BlockPos.ZERO, 0.0F, UUID.randomUUID(), new ArrayList<>());

    public static LinkData fromPlayer(Player player) {
        return new LinkData(player.getCommandSenderWorld().dimension().location(), player.blockPosition(), player.getYRot(), UUID.randomUUID(), new ArrayList<ResourceLocation>());
    }

    public Set<LinkEffect> linkEffectsAsLE(ServerLevel serverLevel) {
        return this.linkEffects.stream().map(serverLevel.registryAccess().registry(LinkEffect.REGISTRY_KEY).get()::get).collect(Collectors.toSet());
    }

    /*public Set<LinkEffect> linkEffectsAsLE(Minecraft minecraft) {
        return this.linkEffects.stream().map(minecraft.getConnection().registryAccess().registry(LinkEffect.REGISTRY_KEY).get()::get).collect(Collectors.toSet());
    }*/

}