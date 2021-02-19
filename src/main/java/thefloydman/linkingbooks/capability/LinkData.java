/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.capability;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
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
import thefloydman.linkingbooks.util.Reference;

public class LinkData {

    @CapabilityInject(ILinkData.class)
    public static final Capability<ILinkData> LINK_DATA = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ILinkData.class, new Storage(), Default::new);
    }

    public static class Default implements ILinkData {

        private ResourceLocation dimension = new ResourceLocation("minecraft:overworld");
        private BlockPos position = Reference.server == null ? BlockPos.ZERO
                : Reference.server.func_241755_D_().getSpawnPoint();
        private float rotation = 0.0F;
        private UUID uuid = UUID.randomUUID();
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
        public void setUUID(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
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

        @Override
        public PacketBuffer write(PacketBuffer buffer) {
            CompoundNBT compound = (CompoundNBT) LINK_DATA.getStorage().writeNBT(LINK_DATA, this, null);
            buffer.writeCompoundTag(compound);
            return buffer;
        }

        @Override
        public void read(PacketBuffer buffer) {
            LINK_DATA.getStorage().readNBT(LINK_DATA, this, null, buffer.readCompoundTag());
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
            for (LinkEffect effect : instance.getLinkEffects()) {
                effectsList.add(StringNBT.valueOf(effect.getRegistryName().toString()));
            }
            nbt.putUniqueId("uuid", instance.getUUID());
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
                if (compound.contains("uuid", NBT.TAG_INT_ARRAY)) {
                    instance.setUUID(compound.getUniqueId("uuid"));
                }
                if (compound.contains("effects", NBT.TAG_LIST)) {
                    for (INBT item : compound.getList("effects", NBT.TAG_STRING)) {
                        instance.addLinkEffect(LinkEffect
                                .get(new ResourceLocation(((StringNBT) item).getString())));
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
