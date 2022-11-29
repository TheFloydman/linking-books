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

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ModCapabilities;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.item.ModItems;

public class LinkingBooksSavedData extends SavedData {

    public Map<UUID, CompoundTag> linkingPanelImages = new HashMap<UUID, CompoundTag>();
    public Map<BlockPos, ILinkData> linkingPortals = new HashMap<BlockPos, ILinkData>();

    public boolean addLinkingPanelImage(UUID uuid, CompoundTag image) {
        if (this.linkingPanelImages.containsKey(uuid)) {
            return false;
        }
        this.linkingPanelImages.put(uuid, image);
        this.setDirty();
        return true;
    }

    public boolean removeLinkingPanelImage(UUID uuid) {
        if (!this.linkingPanelImages.containsKey(uuid)) {
            return false;
        }
        this.linkingPanelImages.remove(uuid);
        this.setDirty();
        return true;
    }

    public CompoundTag getLinkingPanelImage(UUID uuid) {
        return this.linkingPanelImages.get(uuid);
    }

    public boolean addLinkingPortalData(BlockPos pos, ILinkData linkData) {
        this.linkingPortals.put(new BlockPos(pos), linkData);
        this.setDirty();
        return true;
    }

    public boolean removeLinkingPortalData(BlockPos pos) {
        if (!this.linkingPortals.containsKey(pos)) {
            return false;
        }
        this.linkingPortals.remove(pos);
        this.setDirty();
        return true;
    }

    public ILinkData getLinkingPortalData(BlockPos pos) {
        return this.linkingPortals.get(pos);
    }

    public static LinkingBooksSavedData load(CompoundTag nbt) {
        LinkingBooksSavedData data = new LinkingBooksSavedData();
        if (nbt.contains("linkingPanelImages", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("linkingPanelImages", Tag.TAG_COMPOUND);
            for (Tag item : list) {
                CompoundTag compound = (CompoundTag) item;
                if (compound.contains("uuid", Tag.TAG_INT_ARRAY)) {
                    UUID uuid = compound.getUUID("uuid");
                    data.linkingPanelImages.put(uuid, compound);
                }
            }
        }
        if (nbt.contains("linking_portals", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("linking_portals", Tag.TAG_COMPOUND);
            for (Tag item : list) {
                CompoundTag compound = (CompoundTag) item;
                BlockPos pos = NbtUtils.readBlockPos(compound.getCompound("portal_pos"));
                ILinkData linkData = ModItems.BLACK_WRITTEN_LINKING_BOOK.get().getDefaultInstance()
                        .getCapability(ModCapabilities.LINK_DATA).orElse(null);
                ((LinkData) linkData).deserializeNBT(compound.getCompound("link_data"));
                data.linkingPortals.put(pos, linkData);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag imageList = new ListTag();
        linkingPanelImages.forEach((uuid, image) -> {
            image.putUUID("uuid", uuid);
            imageList.add(image);
        });
        nbt.put("linkingPanelImages", imageList);
        ListTag portalList = new ListTag();
        this.linkingPortals.forEach((pos, linkData) -> {
            CompoundTag compound = new CompoundTag();
            compound.put("portal_pos", NbtUtils.writeBlockPos(pos));
            compound.put("link_data", ((LinkData) linkData).serializeNBT());
            portalList.add(compound);
        });
        nbt.put("linking_portals", portalList);
        return nbt;
    }

}
