package thefloydman.linkingbooks.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import javax.annotation.Nonnull;

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
    public @Nonnull ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public @Nonnull BlockState updateShape(@Nonnull BlockState blockState, @Nonnull Direction direction, BlockState blockState2,
                                           @Nonnull LevelAccessor worldAccess, @Nonnull BlockPos blockPos, @Nonnull BlockPos blockPos2) {
        return !blockState2.is(this) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState blockState, @Nonnull BlockGetter blockView, @Nonnull BlockPos blockPos,
                                        @Nonnull CollisionContext shapeContext) {
        return switch (blockState.getValue(AXIS)) {
            case Z -> Z_SHAPE;
            case Y -> Y_SHAPE;
            default -> X_SHAPE;
        };
    }

    /**
     * Based on {@link net.minecraft.world.level.block.EndPortalBlock#entityInside(BlockState, Level, BlockPos, Entity) EndPortalBlock#entityInside}.
     */
    @Override
    public void entityInside(@Nonnull BlockState blockState, @Nonnull Level world, @Nonnull BlockPos blockPos, @Nonnull Entity entity) {
        if (world instanceof ServerLevel && !entity.isPassenger() && !entity.isVehicle()
                && Shapes.joinIsNotEmpty(Shapes.create(
                        entity.getBoundingBox().move((-blockPos.getX()), (-blockPos.getY()), (-blockPos.getZ()))),
                blockState.getShape(world, blockPos), BooleanOp.AND)) {
            LinkingBooksSavedData savedData = ((ServerLevel) world).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);

            LinkingUtils.linkEntity(entity, savedData.getLinkingPortalData(blockPos), false);
        }

    }

    @Override
    public void onRemove(BlockState blockState, @Nonnull Level world, @Nonnull BlockPos pos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() != blockState2.getBlock() && !world.isClientSide()) {
            LinkingBooksSavedData savedData = ((ServerLevel) world).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
            savedData.removeLinkingPortalData(pos);
        }
        super.onRemove(blockState, world, pos, blockState2, bl);
    }

}