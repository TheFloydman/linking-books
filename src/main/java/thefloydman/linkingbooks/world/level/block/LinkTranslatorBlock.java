package thefloydman.linkingbooks.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;
import thefloydman.linkingbooks.world.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.world.level.block.entity.ModBlockEntityTypes;

import javax.annotation.Nonnull;

public class LinkTranslatorBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<LinkTranslatorBlock> CODEC = simpleCodec(LinkTranslatorBlock::new);
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
    protected @Nonnull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_BOOK);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModBlockEntityTypes.LINK_TRANSLATOR.get().create(pos, state);
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
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
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
    public void onRemove(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !world.isClientSide()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof LinkTranslatorBlockEntity translatorTE) {
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
                    if (blockEntity instanceof LinkTranslatorBlockEntity translator) {
                        if (translator.hasBook()) {
                            LinkData linkData = translator.getBook().get(ModDataComponents.LINK_DATA);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

}