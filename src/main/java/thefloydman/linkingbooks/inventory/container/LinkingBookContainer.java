package thefloydman.linkingbooks.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class LinkingBookContainer extends Container {

    public ItemStack book = ItemStack.EMPTY;

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory) {
        super(ModContainerTypes.LINKING_BOOK.get(), windowId);
    }

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory);
        this.book = extraData.readItemStack();
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

}
