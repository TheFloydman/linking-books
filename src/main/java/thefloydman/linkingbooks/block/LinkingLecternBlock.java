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

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.linking.LinkingUtils;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.blockentity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.blockentity.LinkingLecternBlockEntity;
import thefloydman.linkingbooks.blockentity.ModBlockEntityTypes;

import javax.annotation.Nonnull;

public class LinkingLecternBlock extends LecternBlock implements EntityBlock {

    public LinkingLecternBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModBlockEntityTypes.LINKING_LECTERN.get().create(pos, state);
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

}