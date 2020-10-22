package thefloydman.linkingbooks.world.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.util.ImageUtils;
import thefloydman.linkingbooks.util.Reference;

public class LinkingBooksGlobalSavedData extends WorldSavedData {

    private Map<UUID, NativeImage> linkingPanelImages = new HashMap<UUID, NativeImage>();

    public LinkingBooksGlobalSavedData() {
        super(Reference.MOD_ID);
    }

    public LinkingBooksGlobalSavedData(String s) {
        super(s);
    }

    public boolean addLinkingPanelImage(UUID uuid, NativeImage image) {
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

    public NativeImage getLinkingPanelImage(UUID uuid) {
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
                    NativeImage image = ImageUtils.imageFromNBT(compound);
                    linkingPanelImages.put(uuid, image);
                }
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        linkingPanelImages.forEach((uuid, image) -> {
            CompoundNBT compound = ImageUtils.imageToNBT(image);
            compound.putUniqueId("uuid", uuid);
            list.add(compound);
        });
        nbt.put("linkingPanelImages", list);
        return nbt;
    }

}
