/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.blockentity.BlockEntityTypes;
import thefloydman.linkingbooks.blockentity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.capability.Capabilities;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkTranslatorBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
    public static final VoxelShape NORTH_SHAPE = Shapes.or(Block.box(0, 0, 2, 16, 16, 16), Block.box(0, 0, 0, 16, 5, 1),
            Block.box(0, 0, 1, 16, 4, 2), Block.box(0, 11, 0, 16, 16, 1), Block.box(0, 12, 1, 16, 16, 2));
    public static final VoxelShape EAST_SHAPE = Shapes.or(Block.box(0, 0, 0, 14, 16, 16),
            Block.box(15, 0, 0, 16, 5, 16), Block.box(14, 0, 0, 15, 4, 16), Block.box(15, 11, 0, 16, 16, 16),
            Block.box(14, 12, 0, 15, 16, 16));
    public static final VoxelShape SOUTH_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 16, 14),
            Block.box(0, 0, 15, 16, 5, 16), Block.box(0, 0, 14, 16, 4, 15), Block.box(0, 11, 15, 16, 16, 16),
            Block.box(0, 12, 14, 16, 16, 15));
    public static final VoxelShape WEST_SHAPE = Shapes.or(Block.box(2, 0, 0, 16, 16, 16), Block.box(0, 0, 0, 1, 5, 16),
            Block.box(1, 0, 0, 2, 4, 16), Block.box(0, 11, 0, 1, 16, 16), Block.box(1, 12, 0, 2, 16, 16));

    protected LinkTranslatorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_BOOK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_BOOK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypes.LINK_TRANSLATOR.get().create(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity,
            ItemStack itemStack) {
        super.setPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
            for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockEntity blockEntity = world.getBlockEntity(currentPos);
                    if (blockEntity != null && blockEntity instanceof LinkTranslatorBlockEntity) {
                        LinkTranslatorBlockEntity translator = (LinkTranslatorBlockEntity) blockEntity;
                        if (translator.hasBook()) {
                            ILinkData linkData = translator.getBook().getCapability(Capabilities.LINK_DATA)
                                    .orElse(null);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        BlockEntity generic = world.getBlockEntity(pos);
        if (generic instanceof LinkTranslatorBlockEntity) {
            LinkTranslatorBlockEntity blockEntity = (LinkTranslatorBlockEntity) generic;
            if (!world.isClientSide() && hand.equals(InteractionHand.MAIN_HAND) && blockEntity.hasBook()
                    && !player.isShiftKeyDown()) {
                ItemStack stack = blockEntity.getBook();
                Item item = stack.getItem();
                if (item instanceof WrittenLinkingBookItem) {
                    ILinkData linkData = stack.getCapability(Capabilities.LINK_DATA).orElse(null);
                    if (linkData != null) {
                        LinkingUtils.openLinkingBookGui((ServerPlayer) player, false,
                                LinkingBookItem.getColor(stack, 0), linkData, world.dimension().location());

                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    public boolean isPathfindable(BlockGetter blockGetter, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !world.isClientSide()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof LinkTranslatorBlockEntity) {
                LinkTranslatorBlockEntity translatorTE = (LinkTranslatorBlockEntity) tileEntity;
                if (translatorTE.hasBook()) {
                    ItemStack stack = translatorTE.getBook();
                    if (stack.getItem() instanceof WrittenLinkingBookItem) {
                        LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
                        entity.absMoveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                                state.getValue(FACING).toYRot() + 180.0F, 0.0F);
                        world.addFreshEntity(entity);
                    }
                }
            }
        }
        for (int x = pos.getX() - 32; x < pos.getX() + 32; x++) {
            for (int y = pos.getY() - 32; y < pos.getY() + 32; y++) {
                for (int z = pos.getZ() - 32; z < pos.getZ() + 32; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockEntity blockEntity = world.getBlockEntity(currentPos);
                    if (blockEntity != null && blockEntity instanceof LinkTranslatorBlockEntity) {
                        LinkTranslatorBlockEntity translator = (LinkTranslatorBlockEntity) blockEntity;
                        if (translator.hasBook()) {
                            ILinkData linkData = translator.getBook().getCapability(Capabilities.LINK_DATA)
                                    .orElse(null);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case EAST:
                return EAST_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return NORTH_SHAPE;
        }
    }

}
