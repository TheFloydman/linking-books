/*******************************************************************************
 * Linking Books - Fabric
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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import thefloydman.linkingbooks.tileentity.MarkerSwitchTileEntity;
import thefloydman.linkingbooks.tileentity.ModTileEntityTypes;

import net.minecraft.block.AbstractBlock.Properties;

public class MarkerSwitchBlock extends HorizontalBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape SHAPE_BOTTOM;
    public static final VoxelShape SHAPE_TOP;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    static {
        VoxelShape bottom = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
        VoxelShape top = Block.box(2.0D, 1.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        SHAPE_BOTTOM = VoxelShapes.or(bottom, top);
        SHAPE_TOP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    }

    protected MarkerSwitchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, HALF, OPEN);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand handIn, BlockRayTraceResult hit) {
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            state = state.cycle(POWERED);
            world.setBlock(pos, state, 10);
            world.updateNeighborsAt(pos, this);
            world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
            world.updateNeighborsAt(pos.relative(state.getValue(FACING).getCounterClockWise().getOpposite()),
                    this);
            BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
            world.updateNeighborsAt(otherPos, this);
            world.updateNeighborsAt(otherPos.relative(state.getValue(FACING).getOpposite()), this);
            world.updateNeighborsAt(otherPos.relative(state.getValue(FACING).getCounterClockWise().getOpposite()),
                    this);
            world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.5F, 0.5F);
            return ActionResultType.CONSUME;
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * See DoorBlock.neighborChanged().
     */
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
            boolean notify) {
        BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.getBlock() == this) {
            world.setBlockAndUpdate(pos, state.setValue(POWERED, otherState.getValue(POWERED)));
            if ((state.getValue(HALF) == DoubleBlockHalf.LOWER && world.getSignal(pos.below(), Direction.DOWN) > 0)
                    || (state.getValue(HALF) == DoubleBlockHalf.UPPER
                            && world.getSignal(otherPos.below(), Direction.DOWN) > 0)) {
                boolean changed = state.getValue(OPEN) == false;
                world.setBlock(pos, state.setValue(OPEN, true), 10);
                world.setBlock(otherPos, otherState.setValue(OPEN, true), 10);
                if (state.getValue(HALF) == DoubleBlockHalf.LOWER && changed) {
                    world.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.5F,
                            0.5F);
                }
            } else {
                boolean changed = state.getValue(OPEN) == true;
                world.setBlock(pos, state.setValue(OPEN, false), 10);
                world.setBlock(otherPos, otherState.setValue(OPEN, false), 10);
                if (state.getValue(HALF) == DoubleBlockHalf.LOWER && changed) {
                    world.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 0.5F,
                            0.5F);
                }
            }

        }
    }

    /**
     * See DoorBlock.updatePostPlacement().
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, IWorld world,
            BlockPos pos, BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y
                && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return newState.is(this) && newState.getValue(HALF) != doubleBlockHalf ? (state
                    .setValue(FACING, newState.getValue(FACING)).setValue(POWERED, newState.getValue(POWERED)))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN
                    && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState()
                            : super.updateShape(state, direction, newState, world, pos, posFrom);
        }
    }

    @Override
    public int getSignal(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction direction) {
        return direction != Direction.UP && direction != Direction.DOWN
                && (direction == state.getValue(FACING) || direction == state.getValue(FACING).getCounterClockWise())
                && state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction direction) {
        return state.getValue(POWERED)
                && (direction == state.getValue(FACING) || direction == state.getValue(FACING).getCounterClockWise())
                        ? 15
                        : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClientSide() && player.isCreative()) {
            /* Start copy from DoublePlantBlock.removeBottomHalf() */
            DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
            if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = world.getBlockState(blockpos);
                if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
            /* End copy from DoublePlantBlock.removeBottomHalf() */
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.MARKER_SWITCH.get().create();
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && state.getValue(HALF) == DoubleBlockHalf.LOWER && !world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MarkerSwitchTileEntity) {
                MarkerSwitchTileEntity markerTE = (MarkerSwitchTileEntity) tileEntity;
                if (markerTE.hasItem()) {
                    InventoryHelper.dropContents(world, pos, markerTE);
                    world.updateNeighbourForOutputSignal(pos, this);
                }
                super.onRemove(state, world, pos, newState, isMoving);
            }
        }
    }

}
