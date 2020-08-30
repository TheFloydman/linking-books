package thefloydman.linkingbooks.capability;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;

public class LinkData {

    @CapabilityInject(ILinkData.class)
    public static final Capability<ILinkData> LINK_DATA = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ILinkData.class, new Storage(), Default::new);
    }

    public static class Default implements ILinkData {

        private ResourceLocation dimension = new ResourceLocation("overworld");
        private BlockPos position = new BlockPos(0, 0, 0);
        private float rotation = 0.0F;
        private Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();

        @Override
        public void setDimension(ResourceLocation dimension) {
            this.dimension = dimension;
        }

        @Override
        public ResourceLocation getDimension() {
            return this.dimension;
        }

        @Override
        public void setPosition(BlockPos position) {
            this.position = position;
        }

        @Override
        public BlockPos getPosition() {
            return this.position;
        }

        @Override
        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        @Override
        public float getRotation() {
            return this.rotation;
        }

        @Override
        public void setLinkEffects(Set<LinkEffect> effects) {
            this.linkEffects = effects;
        }

        @Override
        public Set<LinkEffect> getLinkEffects() {
            return this.linkEffects;
        }

        @Override
        public boolean addLinkEffect(LinkEffect effect) {
            return this.getLinkEffects().add(effect);
        }

        @Override
        public boolean removeLinkEffect(LinkEffect effect) {
            return this.getLinkEffects().remove(effect);
        }

    }

    public static class Storage implements Capability.IStorage<ILinkData> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<ILinkData> capability, ILinkData instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("dimension",
                    instance.getDimension() == null ? "minecraft:overworld" : instance.getDimension().toString());
            nbt.put("position", NBTUtil
                    .writeBlockPos(instance.getPosition() == null ? new BlockPos(0, 0, 0) : instance.getPosition()));
            nbt.putFloat("rotation", instance.getRotation());
            ListNBT effectsList = new ListNBT();
            instance.getLinkEffects().forEach((effect) -> {
                effectsList.add(StringNBT.valueOf(effect.getRegistryName().toString()));
            });
            nbt.put("effects", effectsList);
            return nbt;
        }

        @Override
        public void readNBT(Capability<ILinkData> capability, ILinkData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT compound = (CompoundNBT) nbt;
                if (compound.contains("dimension", NBT.TAG_STRING)) {
                    instance.setDimension(new ResourceLocation(compound.getString("dimension")));
                }
                if (compound.contains("position", NBT.TAG_COMPOUND)) {
                    instance.setPosition(NBTUtil.readBlockPos(compound.getCompound("position")));
                }
                if (compound.contains("rotation", NBT.TAG_FLOAT)) {
                    instance.setRotation(compound.getFloat("rotation"));
                }
                if (compound.contains("effects", NBT.TAG_LIST)) {
                    for (INBT item : compound.getList("effects", NBT.TAG_STRING)) {
                        instance.addLinkEffect(LinkEffect
                                .getFromResourceLocation(new ResourceLocation(((StringNBT) item).getString())));
                    }
                }
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        private LazyOptional<ILinkData> instance = LazyOptional.of(() -> LINK_DATA.getDefaultInstance());

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap.equals(LINK_DATA) && LINK_DATA != null) {
                return instance.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return LINK_DATA.getStorage().writeNBT(LINK_DATA, instance.orElse(LINK_DATA.getDefaultInstance()), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            LINK_DATA.getStorage().readNBT(LINK_DATA, instance.orElse(LINK_DATA.getDefaultInstance()), null, nbt);
        }

    }

}
