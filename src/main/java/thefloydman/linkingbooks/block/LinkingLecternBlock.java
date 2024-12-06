/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thefloydman.linkingbooks.blockentity.LinkingLecternBlockEntity;
import thefloydman.linkingbooks.blockentity.ModBlockEntityTypes;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.linking.LinkingUtils;

import javax.annotation.Nonnull;

/**
 * Largely copies {@link LecternBlock}.
 */
public class LinkingLecternBlock extends BaseEntityBlock {

    public static final MapCodec<LinkingLecternBlock> CODEC = simpleCodec(LinkingLecternBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final VoxelShape SHAPE_POST = Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
    public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0),
            Block.box(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0),
            Block.box(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0),
            SHAPE_COMMON
    );
    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333),
            Block.box(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667),
            Block.box(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0),
            SHAPE_COMMON
    );
    public static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0),
            Block.box(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0),
            Block.box(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0),
            SHAPE_COMMON
    );
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0),
            Block.box(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667),
            Block.box(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333),
            SHAPE_COMMON
    );

    public LinkingLecternBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected @Nonnull RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @Nonnull VoxelShape getOcclusionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return SHAPE_COMMON;
    }

    @Override
    protected boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @Nonnull VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE_COLLISION;
    }

    @Override
    protected @Nonnull VoxelShape getShape(BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch ((Direction) state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_COMMON;
        };
    }

    @Override
    protected @Nonnull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @Nonnull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nonnull BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModBlockEntityTypes.LINKING_LECTERN.get().create(pos, state);
    }

    @Override
    public @Nonnull MapCodec<LinkingLecternBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nonnull InteractionResult useWithoutItem(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player,
                                                     @Nonnull BlockHitResult hit) {
        BlockEntity generic = world.getBlockEntity(pos);
        if (generic instanceof LinkingLecternBlockEntity blockEntity) {
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

    @Override
    protected boolean isPathfindable(@Nonnull BlockState state, @Nonnull PathComputationType pathComputationType) {
        return false;
    }

}