package thefloydman.linkingbooks.inventory.container;

import net.minecraft.entity.player.PlayerInventory;

public class TestContainer extends BaseContainer {

    public TestContainer(int windowIdIn, PlayerInventory playerInventoryIn) {
        super(windowIdIn, ModContainerTypes.TEST_CONTAINER.get());
    }

}
