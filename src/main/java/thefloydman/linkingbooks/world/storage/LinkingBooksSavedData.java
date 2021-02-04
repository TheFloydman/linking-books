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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.util.Reference;

public class LinkingBooksSavedData extends WorldSavedData {

    private Map<UUID, CompoundNBT> linkingPanelImages = new HashMap<UUID, CompoundNBT>();
    private Map<BlockPos, ILinkData> linkingPortals = new HashMap<BlockPos, ILinkData>();

    public LinkingBooksSavedData() {
        super(Reference.MOD_ID);
    }

    public LinkingBooksSavedData(String s) {
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

    public boolean addLinkingPortalData(BlockPos pos, ILinkData linkData) {
        this.linkingPortals.put(new BlockPos(pos), linkData);
        this.markDirty();
        return true;
    }

    public boolean removeLinkingPortalData(BlockPos pos) {
        if (!this.linkingPortals.containsKey(pos)) {
            return false;
        }
        this.linkingPortals.remove(pos);
        this.markDirty();
        return true;
    }

    public ILinkData getLinkingPortalData(BlockPos pos) {
        return this.linkingPortals.get(pos);
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
        if (nbt.contains("linking_portals", NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("linking_portals", NBT.TAG_COMPOUND);
            for (INBT item : list) {
                CompoundNBT compound = (CompoundNBT) item;
                BlockPos pos = NBTUtil.readBlockPos(compound.getCompound("portal_pos"));
                ILinkData linkData = ModItems.WRITTEN_LINKING_BOOK.get().getDefaultInstance()
                        .getCapability(LinkData.LINK_DATA).orElse(null);
                LinkData.LINK_DATA.readNBT(linkData, null, compound.getCompound("link_data"));
                this.linkingPortals.put(pos, linkData);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT imageList = new ListNBT();
        linkingPanelImages.forEach((uuid, image) -> {
            image.putUniqueId("uuid", uuid);
            imageList.add(image);
        });
        nbt.put("linkingPanelImages", imageList);
        ListNBT portalList = new ListNBT();
        this.linkingPortals.forEach((pos, linkData) -> {
            CompoundNBT compound = new CompoundNBT();
            compound.put("portal_pos", NBTUtil.writeBlockPos(pos));
            compound.put("link_data", LinkData.LINK_DATA.getStorage().writeNBT(LinkData.LINK_DATA, linkData, null));
            portalList.add(compound);
        });
        nbt.put("linking_portals", portalList);
        return nbt;
    }

}
