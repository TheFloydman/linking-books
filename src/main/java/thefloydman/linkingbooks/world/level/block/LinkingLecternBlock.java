package thefloydman.linkingbooks.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;
import thefloydman.linkingbooks.world.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.world.level.block.entity.LinkingLecternBlockEntity;
import thefloydman.linkingbooks.world.level.block.entity.ModBlockEntityTypes;

import javax.annotation.Nonnull;

public class LinkingLecternBlock extends LecternBlock implements EntityBlock {

    public LinkingLecternBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModBlockEntityTypes.LINKING_LECTERN.get().create(pos, state);
    }

    @Override
    public @Nonnull InteractionResult useWithoutItem(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player,
                                                     @Nonnull BlockHitResult hit) {
        BlockEntity generic = world.getBlockEntity(pos);
        if (generic instanceof LinkTranslatorBlockEntity blockEntity) {
            if (!world.isClientSide() && blockEntity.hasBook() && !player.isShiftKeyDown()) {
                ItemStack stack = blockEntity.getBook();
                Item item = stack.getItem();
                if (item instanceof WrittenLinkingBookItem) {
                    LinkData linkData = stack.get(ModDataComponents.LINK_DATA);
                    if (linkData != null) {
                        LinkingUtils.openLinkingBookGui((ServerPlayer) player, false,
                                LinkingUtils.getLinkingBookColor(stack, 0), linkData, world.dimension().location());

                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof LinkingLecternBlockEntity lecternTE) {
                if (lecternTE.hasBook()) {
                    ItemStack stack = lecternTE.getBook();
                    if (stack.getItem() instanceof WrittenLinkingBookItem) {
                        LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setYRot(state.getValue(FACING).toYRot() + 180.0F);
                        world.addFreshEntity(entity);
                    }
                }
                super.onRemove(state, world, pos, newState, isMoving);
            }
        }
    }

}