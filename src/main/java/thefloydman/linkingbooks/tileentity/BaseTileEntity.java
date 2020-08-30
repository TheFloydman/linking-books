package thefloydman.linkingbooks.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class BaseTileEntity extends TileEntity {

    public BaseTileEntity(TileEntityType<? extends BaseTileEntity> tileEntityType) {
        super(tileEntityType);
    }

}
