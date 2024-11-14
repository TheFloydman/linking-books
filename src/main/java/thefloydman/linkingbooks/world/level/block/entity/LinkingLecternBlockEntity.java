package thefloydman.linkingbooks.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LinkingLecternBlockEntity extends LinkingBookHolderBlockEntity {

    public LinkingLecternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LINKING_LECTERN.get(), pos, state);
    }

}