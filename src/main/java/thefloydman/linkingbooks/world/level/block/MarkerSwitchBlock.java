/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thefloydman.linkingbooks.world.level.block.entity.MarkerSwitchBlockEntity;
import thefloydman.linkingbooks.world.level.block.entity.ModBlockEntityTypes;

import javax.annotation.Nonnull;

public class MarkerSwitchBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape SHAPE_BOTTOM;
    public static final VoxelShape SHAPE_TOP;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final MapCodec<MarkerSwitchBlock> CODEC = simpleCodec(MarkerSwitchBlock::new);

    static {
        VoxelShape bottom = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
        VoxelShape top = Block.box(2.0D, 1.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        SHAPE_BOTTOM = Shapes.or(bottom, top);
        SHAPE_TOP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    }

    protected MarkerSwitchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    @Override
    protected @Nonnull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    public @Nonnull VoxelShape getInteractionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    public @Nonnull VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, HALF, OPEN);
    }

    @Override
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState blockState, @Nonnull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @Nonnull InteractionResult useWithoutItem(@Nonnull BlockState blockState, Level level, @Nonnull BlockPos blockPos, @Nonnull Player player,
                                                     @Nonnull BlockHitResult blockHitResult) {
        if (!level.isClientSide() && !player.isShiftKeyDown()) {
            blockState = blockState.cycle(POWERED);
            level.setBlock(blockPos, blockState, 10);
            level.updateNeighborsAt(blockPos, this);
            level.updateNeighborsAt(blockPos.relative(blockState.getValue(FACING).getOpposite()), this);
            level.updateNeighborsAt(blockPos.relative(blockState.getValue(FACING).getCounterClockWise().getOpposite()), this);
            BlockPos otherPos = blockState.getValue(HALF) == DoubleBlockHalf.UPPER ? blockPos.below() : blockPos.above();
            level.updateNeighborsAt(otherPos, this);
            level.updateNeighborsAt(otherPos.relative(blockState.getValue(FACING).getOpposite()), this);
            level.updateNeighborsAt(otherPos.relative(blockState.getValue(FACING).getCounterClockWise().getOpposite()),
                    this);
            level.playSound(null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 0.5F);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, @Nonnull ItemStack
            stack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * See {@link DoorBlock#neighborChanged}.
     */
    @Override
    public void neighborChanged(BlockState blockState, Level level, @Nonnull BlockPos blockPos, @Nonnull Block
                                        block, @Nonnull BlockPos fromPos,
                                boolean notify) {
        BlockPos otherPos = blockState.getValue(HALF) == DoubleBlockHalf.UPPER ? blockPos.below() : blockPos.above();
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.getBlock() == this) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(POWERED, otherState.getValue(POWERED)));
            if ((blockState.getValue(HALF) == DoubleBlockHalf.LOWER && level.getSignal(blockPos.below(), Direction.DOWN) > 0)
                    || (blockState.getValue(HALF) == DoubleBlockHalf.UPPER
                    && level.getSignal(otherPos.below(), Direction.DOWN) > 0)) {
                boolean changed = !blockState.getValue(OPEN);
                level.setBlock(blockPos, blockState.setValue(OPEN, true), 10);
                level.setBlock(otherPos, otherState.setValue(OPEN, true), 10);
                if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER && changed) {
                    level.playSound(null, blockPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                }
            } else {
                boolean changed = blockState.getValue(OPEN);
                level.setBlock(blockPos, blockState.setValue(OPEN, false), 10);
                level.setBlock(otherPos, otherState.setValue(OPEN, false), 10);
                if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER && changed) {
                    level.playSound(null, blockPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 0.5F);
                }
            }

        }
    }

    /**
     * See {@link DoorBlock#updateShape}.
     */
    @Override
    public @Nonnull BlockState updateShape(BlockState state, Direction direction, @Nonnull BlockState
                                                   newState, @Nonnull LevelAccessor world,
                                           @Nonnull BlockPos pos, @Nonnull BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y
                && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return newState.is(this) && newState.getValue(HALF) != doubleBlockHalf
                    ? (state.setValue(FACING, newState.getValue(FACING)).setValue(POWERED, newState.getValue(POWERED)))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN
                    && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, direction, newState, world, pos, posFrom);
        }
    }

    @Override
    public int getSignal(@Nonnull BlockState state, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos
            pos, @Nonnull Direction direction) {
        return direction != Direction.UP && direction != Direction.DOWN
                && (direction == state.getValue(FACING) || direction == state.getValue(FACING).getCounterClockWise())
                && state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos
            pos, @Nonnull Direction direction) {
        return state.getValue(POWERED)
                && (direction == state.getValue(FACING) || direction == state.getValue(FACING).getCounterClockWise())
                ? 15
                : 0;
    }

    @Override
    public boolean isSignalSource(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public @Nonnull BlockState playerWillDestroy(Level level, @Nonnull BlockPos blockPos, @Nonnull BlockState
            blockState, @Nonnull Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            /* Start copy from DoublePlantBlock#preventDropFromBottomPart */
            DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
            if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
                BlockPos blockPosBelow = blockPos.below();
                BlockState blockStateBelow = level.getBlockState(blockPosBelow);
                if (blockStateBelow.getBlock() == blockState.getBlock() && blockStateBelow.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(blockPosBelow, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockPosBelow, Block.getId(blockStateBelow));
                }
            }
            /* End copy from DoublePlantBlock#preventDropFromBottomPart */
        }
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModBlockEntityTypes.MARKER_SWITCH.get().create(pos, state);
    }

    @Override
    public void onRemove(BlockState blockState, @Nonnull Level level, @Nonnull BlockPos blockPos, BlockState newState,
                         boolean isMoving) {
        if (blockState.getBlock() != newState.getBlock() && blockState.getValue(HALF) == DoubleBlockHalf.LOWER
                && !level.isClientSide()) {
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (tileEntity instanceof MarkerSwitchBlockEntity markerTE) {
                if (markerTE.hasItem()) {
                    Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), markerTE.extractItem(0, Integer.MAX_VALUE, false));
                    level.updateNeighbourForOutputSignal(blockPos, this);
                }
                super.onRemove(blockState, level, blockPos, newState, isMoving);
            }
        }
    }

}