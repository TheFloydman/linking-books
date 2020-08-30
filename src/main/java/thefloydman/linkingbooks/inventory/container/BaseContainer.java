package thefloydman.linkingbooks.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class BaseContainer extends Container {

    public BaseContainer(int id, @Nullable ContainerType<? extends BaseContainer> type) {
        super(type, id);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

}
