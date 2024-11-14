package thefloydman.linkingbooks.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;

import javax.annotation.Nonnull;

public class NaraBlock extends Block {

    public NaraBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos blockPos, @Nonnull BlockState blockState, LivingEntity livingEntity,
                            @Nonnull ItemStack itemStack) {
        super.setPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
            for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockEntity blockEntity = world.getBlockEntity(currentPos);
                    if (blockEntity instanceof LinkTranslatorBlockEntity translator) {
                        if (translator.hasBook()) {
                            LinkData linkData = translator.getBook().get(ModDataComponents.LINK_DATA);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState blockState, @Nonnull Level world, @Nonnull BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
                for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                    for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                        BlockPos currentPos = new BlockPos(x, y, z);
                        BlockEntity blockEntity = world.getBlockEntity(currentPos);
                        if (blockEntity instanceof LinkTranslatorBlockEntity translator) {
                            if (translator.hasBook()) {
                                LinkData linkData = translator.getBook().get(ModDataComponents.LINK_DATA);
                                LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData,
                                        translator);
                            }
                        }
                    }
                }
            }
        }
        super.onRemove(blockState, world, blockPos, blockState2, bl);
    }

}