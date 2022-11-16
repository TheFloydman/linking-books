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

import javax.annotation.Nullable;

import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import thefloydman.linkingbooks.api.capability.IColorCapability;

public class ColorCapability {

    @CapabilityInject(IColorCapability.class)
    public static final Capability<IColorCapability> COLOR = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IColorCapability.class, new Storage(), Default::new);
    }

    public static class Default implements IColorCapability {

        private int color = DyeColor.GREEN.getColorValue();

        @Override
        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public int getColor() {
            return this.color;
        }

        @Override
        public CompoundNBT writeToShareTag(CompoundNBT nbt) {
            CompoundNBT tag = new CompoundNBT();
            if (nbt != null) {
                tag = nbt.copy();
            }
            tag.put("color", COLOR.writeNBT(this, null));
            return tag;
        }

        @Override
        public void readFromShareTag(CompoundNBT nbt) {
            if (nbt != null && nbt.contains("color", NBT.TAG_INT)) {
                COLOR.readNBT(this, null, nbt.get("color"));
            }
        }
    }

    public static class Storage implements Capability.IStorage<IColorCapability> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IColorCapability> capability, IColorCapability instance, Direction side) {
            return IntNBT.valueOf(instance.getColor());
        }

        @Override
        public void readNBT(Capability<IColorCapability> capability, IColorCapability instance, Direction side,
                INBT nbt) {
            if (nbt instanceof IntNBT) {
                instance.setColor(((IntNBT) nbt).getAsInt());
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        private LazyOptional<IColorCapability> instance = LazyOptional.of(() -> COLOR.getDefaultInstance());

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap.equals(COLOR) && COLOR != null) {
                return instance.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return COLOR.getStorage().writeNBT(COLOR, instance.orElse(COLOR.getDefaultInstance()), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            COLOR.getStorage().readNBT(COLOR, instance.orElse(COLOR.getDefaultInstance()), null, nbt);
        }

    }

}
