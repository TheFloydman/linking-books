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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.tileentity.LinkTranslatorTileEntity;
import thefloydman.linkingbooks.tileentity.ModTileEntityTypes;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;

import net.minecraft.block.AbstractBlock.Properties;

public class LinkTranslatorBlock extends HorizontalBlock {

    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
    public static final VoxelShape NORTH_SHAPE = VoxelShapes.or(Block.box(0, 0, 2, 16, 16, 16),
            Block.box(0, 0, 0, 16, 5, 1), Block.box(0, 0, 1, 16, 4, 2),
            Block.box(0, 11, 0, 16, 16, 1), Block.box(0, 12, 1, 16, 16, 2));
    public static final VoxelShape EAST_SHAPE = VoxelShapes.or(Block.box(0, 0, 0, 14, 16, 16),
            Block.box(15, 0, 0, 16, 5, 16), Block.box(14, 0, 0, 15, 4, 16),
            Block.box(15, 11, 0, 16, 16, 16), Block.box(14, 12, 0, 15, 16, 16));
    public static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(Block.box(0, 0, 0, 16, 16, 14),
            Block.box(0, 0, 15, 16, 5, 16), Block.box(0, 0, 14, 16, 4, 15),
            Block.box(0, 11, 15, 16, 16, 16), Block.box(0, 12, 14, 16, 16, 15));
    public static final VoxelShape WEST_SHAPE = VoxelShapes.or(Block.box(2, 0, 0, 16, 16, 16),
            Block.box(0, 0, 0, 1, 5, 16), Block.box(1, 0, 0, 2, 4, 16),
            Block.box(0, 11, 0, 1, 16, 16), Block.box(1, 12, 0, 2, 16, 16));

    protected LinkTranslatorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_BOOK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_BOOK);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.LINK_TRANSLATOR.get().create();
    }

    @Override
    public void setPlacedBy(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity,
            ItemStack itemStack) {
        super.setPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        for (int x = blockPos.getX() - 32; x < blockPos.getX() + 32; x++) {
            for (int y = blockPos.getY() - 32; y < blockPos.getY() + 32; y++) {
                for (int z = blockPos.getZ() - 32; z < blockPos.getZ() + 32; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    TileEntity blockEntity = world.getBlockEntity(currentPos);
                    if (blockEntity != null && blockEntity instanceof LinkTranslatorTileEntity) {
                        LinkTranslatorTileEntity translator = (LinkTranslatorTileEntity) blockEntity;
                        if (translator.hasBook()) {
                            ILinkData linkData = translator.getBook().getCapability(LinkData.LINK_DATA).orElse(null);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, BlockRayTraceResult hit) {
        TileEntity generic = world.getBlockEntity(pos);
        if (generic instanceof LinkTranslatorTileEntity) {
            LinkTranslatorTileEntity blockEntity = (LinkTranslatorTileEntity) generic;
            if (!world.isClientSide() && hand.equals(Hand.MAIN_HAND) && blockEntity.hasBook() && !player.isShiftKeyDown()) {
                ItemStack stack = blockEntity.getBook();
                Item item = stack.getItem();
                if (item instanceof WrittenLinkingBookItem) {
                    ILinkData linkData = stack.getCapability(LinkData.LINK_DATA).orElse(null);
                    IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
                    if (linkData != null && color != null) {
                        LinkingUtils.openLinkingBookGui((ServerPlayerEntity) player, false, color.getColor(), linkData,
                                world.dimension().location());

                    }
                }
            }
        }

        return ActionResultType.PASS;
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
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof LinkTranslatorTileEntity) {
                LinkTranslatorTileEntity translatorTE = (LinkTranslatorTileEntity) tileEntity;
                if (Reference.isImmersivePortalsLoaded()) {
                    ImmersivePortalsIntegration.deleteLinkingPortals(translatorTE);
                }
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
                    TileEntity blockEntity = world.getBlockEntity(currentPos);
                    if (blockEntity != null && blockEntity instanceof LinkTranslatorTileEntity) {
                        LinkTranslatorTileEntity translator = (LinkTranslatorTileEntity) blockEntity;
                        if (Reference.isImmersivePortalsLoaded()) {
                            ImmersivePortalsIntegration.deleteLinkingPortals(translator);
                        }
                        if (translator.hasBook()) {
                            ILinkData linkData = translator.getBook().getCapability(LinkData.LINK_DATA).orElse(null);
                            LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, currentPos, linkData, translator);
                        }
                    }
                }
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
