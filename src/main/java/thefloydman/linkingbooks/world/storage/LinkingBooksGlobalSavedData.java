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
