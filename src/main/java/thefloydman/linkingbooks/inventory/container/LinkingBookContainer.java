package thefloydman.linkingbooks.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;

public class LinkingBookContainer extends Container {

    public boolean holdingBook = false;
    public int bookColor = DyeColor.GREEN.getColorValue();
    public ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory) {
        super(ModContainerTypes.LINKING_BOOK.get(), windowId);
    }

    public LinkingBookContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory);
        this.holdingBook = extraData.readBoolean();
        this.bookColor = extraData.readInt();
        this.linkData.read(extraData);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

}
