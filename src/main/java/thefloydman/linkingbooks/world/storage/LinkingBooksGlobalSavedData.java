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
package thefloydman.linkingbooks.world.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.util.Reference;

public class LinkingBooksGlobalSavedData extends WorldSavedData {

    private Map<UUID, CompoundNBT> linkingPanelImages = new HashMap<UUID, CompoundNBT>();

    public LinkingBooksGlobalSavedData() {
        super(Reference.MOD_ID);
    }

    public LinkingBooksGlobalSavedData(String s) {
        super(s);
    }

    public boolean addLinkingPanelImage(UUID uuid, CompoundNBT image) {
        if (this.linkingPanelImages.containsKey(uuid)) {
            return false;
        }
        this.linkingPanelImages.put(uuid, image);
        this.markDirty();
        return true;
    }

    public boolean removeLinkingPanelImage(UUID uuid) {
        if (!this.linkingPanelImages.containsKey(uuid)) {
            return false;
        }
        this.linkingPanelImages.remove(uuid);
        this.markDirty();
        return true;
    }

    public CompoundNBT getLinkingPanelImage(UUID uuid) {
        return this.linkingPanelImages.get(uuid);
    }

    @Override
    public void read(CompoundNBT nbt) {
        if (nbt.contains("linkingPanelImages", NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("linkingPanelImages", NBT.TAG_COMPOUND);
            for (INBT item : list) {
                CompoundNBT compound = (CompoundNBT) item;
                if (compound.contains("uuid", NBT.TAG_INT_ARRAY)) {
                    UUID uuid = compound.getUniqueId("uuid");
                    linkingPanelImages.put(uuid, compound);
                }
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        linkingPanelImages.forEach((uuid, image) -> {
            image.putUniqueId("uuid", uuid);
            list.add(image);
        });
        nbt.put("linkingPanelImages", list);
        return nbt;
    }

}
