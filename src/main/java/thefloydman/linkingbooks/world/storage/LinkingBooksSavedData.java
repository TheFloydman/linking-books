/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.level.saveddata.SavedData;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.world.generation.AgeInfo;

import javax.annotation.Nonnull;
import java.util.*;

public class LinkingBooksSavedData extends SavedData {

    public final Map<UUID, CompoundTag> linkingPanelImages = new HashMap<>();
    public final Map<BlockPos, LinkData> linkingPortals = new HashMap<>();
    public final Set<AgeInfo> ages = Sets.newHashSet();
    public final Map<UUID, String> playerDisplayNames = new HashMap<>();

    private LinkingBooksSavedData() {
    }

    public LinkingBooksSavedData(Map<UUID, CompoundTag> linkingPanelImages, Map<BlockPos, LinkData> linkingPortals) {

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

    public void addAge(AgeInfo age) {
        if (this.ages.stream().map(AgeInfo::id).noneMatch(resourceLocation -> resourceLocation.equals(age.id()))) {
            this.ages.add(age);
            this.setDirty();
        }
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

    public boolean addPlayerDisplayName(UUID uuid, String displayName) {
        this.playerDisplayNames.put(uuid, displayName);
        this.setDirty();
        return true;
    }

    public String getPlayerDisplayName(UUID uuid) {
        return this.playerDisplayNames.get(uuid);
    }

    // TODO: Finish codec.
    public static final Codec<LinkingBooksSavedData> CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            Codec.list(CompoundTag.CODEC).fieldOf("linkingPanelImages").forGetter(
                                    linkingBooksSavedData -> linkingBooksSavedData.linkingPanelImages.entrySet().stream().map(entry -> {
                                        CompoundTag compoundTag = entry.getValue();
                                        compoundTag.putUUID("uuid", entry.getKey());
                                        return compoundTag;
                                    }).toList()
                            ),
                            Codec.list(CompoundTag.CODEC).fieldOf("linking_portals").forGetter(
                                    linkingBooksSavedData -> linkingBooksSavedData.linkingPortals.entrySet().stream().map(
                                            entry -> {
                                                CompoundTag compoundTag = new CompoundTag();
                                                compoundTag.put("portal_pos", NbtUtils.writeBlockPos(entry.getKey()));
                                                LinkData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).ifSuccess(tag -> compoundTag.put("link_data", tag));
                                                return compoundTag;
                                            }
                                    ).toList()
                            )
                    )
                    .apply(codecBuilderInstance, (linkingPanelImagesList, linkingPortalsList) -> {
                        Map<UUID, CompoundTag> linkingPanelImages = new HashMap<>();
                        for (CompoundTag compoundTag : linkingPanelImagesList) {
                            UUID uuid = compoundTag.getUUID("uuid");
                            compoundTag.remove("uuid");
                            linkingPanelImages.put(uuid, compoundTag);
                        }
                        Map<BlockPos, LinkData> linkingPortals = new HashMap<>();
                        for (CompoundTag compound : linkingPortalsList) {
                            BlockPos pos = NbtUtils.readBlockPos(compound, "portal_pos").orElseGet(() -> BlockPos.ZERO);
                            try {
                                LinkData linkData = LinkData.CODEC.parse(NbtOps.INSTANCE, compound.getCompound("link_data")).getOrThrow();
                                linkingPortals.put(pos, linkData);
                            } catch (IllegalStateException e) {
                                LogUtils.getLogger().warn(e.getMessage());
                            }
                        }
                        return new LinkingBooksSavedData(linkingPanelImages, linkingPortals);
                    })
    );

    @Override
    public @Nonnull CompoundTag save(CompoundTag nbt, @Nonnull HolderLookup.Provider provider) {

        ListTag imageList = new ListTag();
        this.linkingPanelImages.forEach((uuid, image) -> {
            image.putUUID("uuid", uuid);
            imageList.add(image);
        });
        nbt.put("linkingPanelImages", imageList);

        ListTag portalList = new ListTag();
        this.linkingPortals.forEach((pos, linkData) -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("portal_pos", NbtUtils.writeBlockPos(pos));
            LinkData.CODEC.encodeStart(NbtOps.INSTANCE, linkData).ifSuccess(tag -> compoundTag.put("link_data", tag));
            portalList.add(compoundTag);
        });
        nbt.put("linking_portals", portalList);

        ListTag ageList = new ListTag();
        List<Tag> tags = this.ages.stream().map(age -> {
            DataResult<Tag> dataResult = AgeInfo.CODEC.encodeStart(NbtOps.INSTANCE, age);
            if (dataResult.isSuccess() && dataResult.result().isPresent()) {
                return dataResult.result().get();
            }
            return null;
        }).filter(Objects::nonNull).toList();
        ageList.addAll(tags);
        nbt.put("ages", ageList);

        ListTag displayNamesList = new ListTag();
        this.playerDisplayNames.forEach((uuid, displayName) -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("uuid", uuid);
            compoundTag.putString("display_name", displayName);
            displayNamesList.add(compoundTag);
        });
        nbt.put("player_display_names", displayNamesList);

        return nbt;

    }

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

        if (nbt.contains("ages", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("ages", Tag.TAG_COMPOUND);
            data.ages.addAll(list.stream().map(tag -> {
                DataResult<Pair<AgeInfo, Tag>> pair = AgeInfo.CODEC.decode(NbtOps.INSTANCE, tag);
                if (pair.isSuccess() && pair.result().isPresent()) {
                    return pair.result().get().getFirst();
                }
                return AgeInfo.DUMMY;
            }).toList());
        }

        if (nbt.contains("player_display_names", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("player_display_names", Tag.TAG_COMPOUND);
            for (Tag item : list) {
                CompoundTag compound = (CompoundTag) item;
                if (compound.contains("uuid", Tag.TAG_INT_ARRAY) && compound.contains("display_name", Tag.TAG_STRING)) {
                    UUID uuid = compound.getUUID("uuid");
                    String displayName = compound.getString("display_name");
                    data.playerDisplayNames.put(uuid, displayName);
                }
            }
        }

        return data;

    }

}
