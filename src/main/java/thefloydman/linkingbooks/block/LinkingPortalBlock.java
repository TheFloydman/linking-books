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
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Much of this code is copied from
 * {@link net.minecraft.block.NetherPortalBlock}.
 *
 */
public class LinkingPortalBlock extends Block {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    protected static final VoxelShape X_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Y_SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public LinkingPortalBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getCloneItemStack(IBlockReader blockView, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2,
            IWorld worldAccess, BlockPos blockPos, BlockPos blockPos2) {
        return !blockState2.is(this) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader blockView, BlockPos blockPos,
            ISelectionContext shapeContext) {
        switch (blockState.getValue(AXIS)) {
            case Z:
                return Z_SHAPE;
            case Y:
                return Y_SHAPE;
            case X:
            default:
                return X_SHAPE;
        }
    }

    /**
     * Copied from {@link net.minecraft.block.EndPortalBlock}.
     *
     */
    @Override
    public void entityInside(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (world instanceof ServerWorld && !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()
                && VoxelShapes.joinIsNotEmpty(VoxelShapes.create(
                        entity.getBoundingBox().move((-blockPos.getX()), (-blockPos.getY()), (-blockPos.getZ()))),
                        blockState.getShape(world, blockPos), IBooleanFunction.AND)) {
            LinkingBooksSavedData savedData = ((ServerWorld) world).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::new, Reference.MOD_ID);

            LinkingUtils.linkEntity(entity, savedData.getLinkingPortalData(blockPos), false);
        }

    }

    @Override
    public void onRemove(BlockState blockState, World world, BlockPos pos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() != blockState2.getBlock() && !world.isClientSide()) {
            LinkingBooksSavedData savedData = ((ServerWorld) world).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::new, Reference.MOD_ID);
            savedData.removeLinkingPortalData(pos);
        }
        super.onRemove(blockState, world, pos, blockState2, bl);
    }

}
