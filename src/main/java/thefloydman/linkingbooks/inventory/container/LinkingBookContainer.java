package thefloydman.linkingbooks.inventory.container;

import net.minecraft.entity.player.PlayerInventory;

public class LinkingBookContainer extends BaseContainer {

    public LinkingBookContainer(int windowIdIn, PlayerInventory playerInventoryIn) {
        super(windowIdIn, ModContainerTypes.LINKING_BOOK.get());
    }

}
