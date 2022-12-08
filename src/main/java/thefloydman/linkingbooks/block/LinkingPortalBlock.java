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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

/**
 * Much of this code is based on {@link net.minecraft.block.NetherPortalBlock}.
 *
 */
@SuppressWarnings("deprecation")
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getCloneItemStack(BlockGetter blockView, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2,
            LevelAccessor worldAccess, BlockPos blockPos, BlockPos blockPos2) {
        return !blockState2.is(this) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos,
            CollisionContext shapeContext) {
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
    public void entityInside(BlockState blockState, Level world, BlockPos blockPos, Entity entity) {
        if (world instanceof ServerLevel && !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()
                && Shapes.joinIsNotEmpty(Shapes.create(
                        entity.getBoundingBox().move((-blockPos.getX()), (-blockPos.getY()), (-blockPos.getZ()))),
                        blockState.getShape(world, blockPos), BooleanOp.AND)) {
            LinkingBooksSavedData savedData = ((ServerLevel) world).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::load, LinkingBooksSavedData::new, Reference.MOD_ID);

            LinkingUtils.linkEntity(entity, savedData.getLinkingPortalData(blockPos), false);
        }

    }

    @Override
    public void onRemove(BlockState blockState, Level world, BlockPos pos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() != blockState2.getBlock() && !world.isClientSide()) {
            LinkingBooksSavedData savedData = ((ServerLevel) world).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::load, LinkingBooksSavedData::new, Reference.MOD_ID);
            savedData.removeLinkingPortalData(pos);
        }
        super.onRemove(blockState, world, pos, blockState2, bl);
    }

}
