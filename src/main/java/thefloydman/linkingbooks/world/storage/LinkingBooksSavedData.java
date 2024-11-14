package thefloydman.linkingbooks.world.storage;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.CommandStorage;
import org.jetbrains.annotations.NotNull;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.ImageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkingBooksSavedData extends SavedData {

    public Map<UUID, CompoundTag> linkingPanelImages = new HashMap<>();
    public Map<BlockPos, LinkData> linkingPortals = new HashMap<>();

    public boolean addLinkingPanelImage(UUID uuid, NativeImage image) {
        if (this.linkingPanelImages.containsKey(uuid)) {
            return false;
        }
        this.linkingPanelImages.put(uuid, (CompoundTag) ImageUtils.NATIVE_IMAGE_CODEC.encodeStart(NbtOps.INSTANCE, image).getOrThrow());
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

    public NativeImage getLinkingPanelImage(UUID uuid) {
        return ImageUtils.NATIVE_IMAGE_CODEC.parse(NbtOps.INSTANCE, this.linkingPanelImages.get(uuid)).getOrThrow();
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
            try {
                compound.put("link_data", LinkData.CODEC.encodeStart(NbtOps.INSTANCE, linkData).getOrThrow());
            } catch (IllegalStateException exception) {
                LogUtils.getLogger().warn(exception.getMessage());
            }
            portalList.add(compound);
        });
        nbt.put("linking_portals", portalList);
        return nbt;
    }

    public static SavedData.Factory<LinkingBooksSavedData> factory() {
        return new SavedData.Factory<>(
                LinkingBooksSavedData::new, LinkingBooksSavedData::load
        );
    }

}
