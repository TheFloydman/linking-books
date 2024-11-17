/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.data;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.linking.LinkEffect;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public record LinkData(@Nonnull ResourceLocation dimension, @Nonnull BlockPos blockPos, float rotation,
                       @Nonnull UUID uuid,
                       @Nonnull List<ResourceLocation> linkEffects) {

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
        return this.linkEffects.stream().map(LinkEffect::getLinkEffect).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @OnlyIn(Dist.CLIENT)
    public Set<LinkEffect> linkEffectsAsLE(Minecraft minecraft) {
        return this.linkEffects.stream().map(minecraft.getConnection().registryAccess().registry(LinkEffect.REGISTRY_KEY).get()::get).collect(Collectors.toSet());
    }

}