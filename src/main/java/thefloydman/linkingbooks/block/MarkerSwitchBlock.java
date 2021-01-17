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

public class MarkerSwitchBlock extends HorizontalBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape SHAPE_BOTTOM;
    public static final VoxelShape SHAPE_TOP;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    static {
        VoxelShape bottom = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
        VoxelShape top = Block.makeCuboidShape(2.0D, 1.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        SHAPE_BOTTOM = VoxelShapes.or(bottom, top);
        SHAPE_TOP = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    }

    protected MarkerSwitchBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH)
                .with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER).with(OPEN, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_BOTTOM;
        }
        return SHAPE_TOP;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, POWERED, HALF, OPEN);
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote() && !player.isSneaking()) {
            state = state.func_235896_a_(POWERED);
            world.setBlockState(pos, state, 10);
            world.notifyNeighborsOfStateChange(pos, this);
            world.notifyNeighborsOfStateChange(pos.offset(state.get(HORIZONTAL_FACING).getOpposite()), this);
            world.notifyNeighborsOfStateChange(pos.offset(state.get(HORIZONTAL_FACING).rotateYCCW().getOpposite()),
                    this);
            BlockPos otherPos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
            world.notifyNeighborsOfStateChange(otherPos, this);
            world.notifyNeighborsOfStateChange(otherPos.offset(state.get(HORIZONTAL_FACING).getOpposite()), this);
            world.notifyNeighborsOfStateChange(otherPos.offset(state.get(HORIZONTAL_FACING).rotateYCCW().getOpposite()),
                    this);
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.5F, 0.5F);
            return ActionResultType.CONSUME;
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    /**
     * See DoorBlock.neighborChanged().
     */
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
            boolean notify) {
        BlockPos otherPos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.getBlock() == this) {
            world.setBlockState(pos, state.with(POWERED, otherState.get(POWERED)));
            if ((state.get(HALF) == DoubleBlockHalf.LOWER && world.getRedstonePower(pos.down(), Direction.DOWN) > 0)
                    || (state.get(HALF) == DoubleBlockHalf.UPPER
                            && world.getRedstonePower(otherPos.down(), Direction.DOWN) > 0)) {
                boolean changed = state.get(OPEN) == false;
                world.setBlockState(pos, state.with(OPEN, true), 10);
                world.setBlockState(otherPos, otherState.with(OPEN, true), 10);
                if (state.get(HALF) == DoubleBlockHalf.LOWER && changed) {
                    world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.5F,
                            0.5F);
                }
            } else {
                boolean changed = state.get(OPEN) == true;
                world.setBlockState(pos, state.with(OPEN, false), 10);
                world.setBlockState(otherPos, otherState.with(OPEN, false), 10);
                if (state.get(HALF) == DoubleBlockHalf.LOWER && changed) {
                    world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 0.5F,
                            0.5F);
                }
            }

        }
    }

    /**
     * See DoorBlock.updatePostPlacement().
     */
    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState newState, IWorld world,
            BlockPos pos, BlockPos posFrom) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y
                && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return newState.isIn(this) && newState.get(HALF) != doubleBlockHalf ? (state
                    .with(HORIZONTAL_FACING, newState.get(HORIZONTAL_FACING)).with(POWERED, newState.get(POWERED)))
                    : Blocks.AIR.getDefaultState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN
                    && !state.isValidPosition(world, pos) ? Blocks.AIR.getDefaultState()
                            : super.updatePostPlacement(state, direction, newState, world, pos, posFrom);
        }
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction direction) {
        return direction != Direction.UP && direction != Direction.DOWN
                && (direction == state.get(HORIZONTAL_FACING) || direction == state.get(HORIZONTAL_FACING).rotateYCCW())
                && state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction direction) {
        return state.get(POWERED)
                && (direction == state.get(HORIZONTAL_FACING) || direction == state.get(HORIZONTAL_FACING).rotateYCCW())
                        ? 15
                        : 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isRemote() && player.isCreative()) {
            /* Start copy from DoublePlantBlock.removeBottomHalf() */
            DoubleBlockHalf doubleblockhalf = state.get(HALF);
            if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.down();
                BlockState blockstate = world.getBlockState(blockpos);
                if (blockstate.getBlock() == state.getBlock() && blockstate.get(HALF) == DoubleBlockHalf.LOWER) {
                    world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                    world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
                }
            }
            /* End copy from DoublePlantBlock.removeBottomHalf() */
        }
        super.onBlockHarvested(world, pos, state, player);
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
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && state.get(HALF) == DoubleBlockHalf.LOWER && !world.isRemote()) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof MarkerSwitchTileEntity) {
                MarkerSwitchTileEntity markerTE = (MarkerSwitchTileEntity) tileEntity;
                if (markerTE.hasItem()) {
                    InventoryHelper.dropInventoryItems(world, pos, markerTE);
                    world.updateComparatorOutputLevel(pos, this);
                }
                super.onReplaced(state, world, pos, newState, isMoving);
            }
        }
    }

}
