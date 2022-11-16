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

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class LinkingBookCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private LazyOptional<LinkData> linkData = LazyOptional.of(LinkData::new);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap.equals(Capabilities.LINK_DATA) && Capabilities.LINK_DATA != null) {
            return this.linkData.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        LinkData linkCap = this.linkData.orElse(null);
        if (linkCap != null) {
            compound.put("link_data", linkCap.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("link_data", Tag.TAG_COMPOUND)) {
            linkData = LazyOptional.of(() -> getLinkData(nbt));
        }
    }

    private LinkData getLinkData(CompoundTag nbt) {
        LinkData cap = new LinkData();
        if (nbt.contains("link_data", Tag.TAG_COMPOUND)) {
            cap.deserializeNBT(nbt.getCompound("link_data"));
        }
        return cap;
    }

}