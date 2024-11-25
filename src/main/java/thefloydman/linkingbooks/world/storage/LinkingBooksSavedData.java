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

package thefloydman.linkingbooks.world.storage;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import thefloydman.linkingbooks.data.LinkData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkingBooksSavedData extends SavedData {

    public final Map<UUID, CompoundTag> linkingPanelImages = new HashMap<>();
    public final Map<BlockPos, LinkData> linkingPortals = new HashMap<>();

    public static LinkingBooksSavedData load(CompoundTag nbt, HolderLookup.Provider provider) {
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
                BlockPos pos = NbtUtils.readBlockPos(compound, "portal_pos").orElseGet(() -> BlockPos.ZERO);
                try {
                    LinkData linkData = LinkData.CODEC.parse(NbtOps.INSTANCE, compound.getCompound("link_data")).getOrThrow();
                    data.linkingPortals.put(pos, linkData);
                } catch (IllegalStateException exception) {
                    LogUtils.getLogger().warn(exception.getMessage());
                }
            }
        }
        return data;
    }

    public static SavedData.Factory<LinkingBooksSavedData> factory() {
        return new SavedData.Factory<>(
                LinkingBooksSavedData::new, LinkingBooksSavedData::load
        );
    }

    public boolean addLinkingPanelImage(UUID uuid, CompoundTag image) {
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

    public boolean addLinkingPortalData(BlockPos pos, LinkData linkData) {
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

    public LinkData getLinkingPortalData(BlockPos pos) {
        return this.linkingPortals.get(pos);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
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
            LinkData.CODEC.encodeStart(NbtOps.INSTANCE, linkData).ifSuccess(tag -> compound.put("link_data", tag));
            portalList.add(compound);
        });
        nbt.put("linking_portals", portalList);
        return nbt;
    }

}
