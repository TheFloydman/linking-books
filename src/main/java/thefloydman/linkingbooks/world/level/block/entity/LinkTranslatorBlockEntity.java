package thefloydman.linkingbooks.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LinkTranslatorBlockEntity extends LinkingBookHolderBlockEntity {

    public LinkTranslatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LINK_TRANSLATOR.get(), pos, state);
    }

}