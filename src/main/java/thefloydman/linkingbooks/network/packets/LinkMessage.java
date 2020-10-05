package thefloydman.linkingbooks.network.packets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkMessage implements IMessage {

    public boolean holdingBook = false;
    public String dimension = "minecraft:overworld";
    public BlockPos blockPos = new BlockPos(0, 0, 0);
    public float rotation = 0.0F;
    public List<String> linkEffects = new ArrayList<String>();

    public LinkMessage(boolean holdingBook, String dimension, BlockPos pos, float rotation, List<String> linkEffects) {
        this.holdingBook = holdingBook;
        this.dimension = dimension;
        this.blockPos = pos;
        this.rotation = rotation;
        this.linkEffects = linkEffects;
    }

    public LinkMessage() {
        this(false, "minecraft:overworld", new BlockPos(0, 0, 0), 0.0F, new ArrayList<String>());
    }

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        buffer.writeBoolean(this.holdingBook);
        buffer.writeString(this.dimension);
        buffer.writeBlockPos(this.blockPos);
        buffer.writeFloat(this.rotation);
        ListNBT list = new ListNBT();
        for (String string : this.linkEffects) {
            list.add(StringNBT.valueOf(string));
        }
        CompoundNBT compound = new CompoundNBT();
        compound.put("effects", list);
        buffer.writeCompoundTag(compound);
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.holdingBook = buffer.readBoolean();
        this.dimension = buffer.readString();
        this.blockPos = buffer.readBlockPos();
        this.rotation = buffer.readFloat();
        CompoundNBT compound = buffer.readCompoundTag();
        if (compound.contains("effects", NBT.TAG_LIST)) {
            ListNBT list = compound.getList("effects", NBT.TAG_STRING);
            for (INBT string : list) {
                this.linkEffects.add(string.getString());
            }
        }
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
            linkData.setDimension(new ResourceLocation(this.dimension));
            linkData.setPosition(this.blockPos);
            linkData.setRotation(this.rotation);
            Set<LinkEffect> effects = new HashSet<LinkEffect>();
            for (String string : this.linkEffects) {
                LinkEffect effect = LinkEffect.getFromResourceLocation(new ResourceLocation(string));
                if (effect != null) {
                    effects.add(effect);
                }
            }
            linkData.setLinkEffects(effects);

            ServerPlayerEntity player = ctx.getSender();
            if (player != null && this.holdingBook) {
                World world = player.getEntityWorld();
                LinkingBookEntity entity = new LinkingBookEntity(world, player.getHeldItemMainhand().copy());
                Vector3d lookVec = player.getLookVec();
                entity.setPosition(player.getPosX() + (lookVec.getX() / 4.0D), player.getPosY() + 1.0D,
                        player.getPosZ() + (lookVec.getZ() / 4.0D));
                entity.rotationYaw = player.rotationYawHead;
                world.addEntity(entity);
                player.getHeldItemMainhand().shrink(1);
            }

            LinkingUtils.linkEntity(ctx.getSender(), linkData);
        });
    }

}
