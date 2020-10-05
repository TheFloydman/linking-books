package thefloydman.linkingbooks.inventory.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public class LinkingBookContainer extends Container {

    public boolean holdingBook = false;
    public DyeColor bookColor = DyeColor.GREEN;
    public String dimension = "minecraft:overworld";
    public BlockPos blockPos = new BlockPos(0, 0, 0);
    public float rotation = 0.0F;
    public List<String> linkEffects = new ArrayList<String>();

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory) {
        super(ModContainerTypes.LINKING_BOOK.get(), windowId);
    }

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory);
        this.holdingBook = extraData.readBoolean();
        this.bookColor = extraData.readEnumValue(DyeColor.class);
        this.dimension = extraData.readString();
        this.blockPos = extraData.readBlockPos();
        this.rotation = extraData.readFloat();
        CompoundNBT compound = extraData.readCompoundTag();
        if (compound.contains("effects", NBT.TAG_LIST)) {
            ListNBT list = compound.getList("effects", NBT.TAG_STRING);
            for (INBT string : list) {
                linkEffects.add(string.getString());
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

}
