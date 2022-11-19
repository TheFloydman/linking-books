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

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.blockentity.ModBlockEntityTypes;
import thefloydman.linkingbooks.blockentity.LinkingLecternBlockEntity;
import thefloydman.linkingbooks.capability.ModCapabilities;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkingLecternBlock extends LecternBlock implements EntityBlock {

    public LinkingLecternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.LINKING_LECTERN.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult result) {
        BlockEntity generic = world.getBlockEntity(pos);
        if (generic instanceof LinkingLecternBlockEntity) {
            LinkingLecternBlockEntity tileEntity = (LinkingLecternBlockEntity) generic;
            if (!world.isClientSide() && hand.equals(InteractionHand.MAIN_HAND) && tileEntity.hasBook()
                    && !player.isShiftKeyDown()) {
                ItemStack stack = tileEntity.getBook();
                Item item = stack.getItem();
                if (item instanceof WrittenLinkingBookItem) {
                    ILinkData linkData = stack.getCapability(ModCapabilities.LINK_DATA).orElse(null);
                    if (linkData != null) {
                        LinkingUtils.openLinkingBookGui((ServerPlayer) player, false,
                                LinkingBookItem.getColor(stack, 0), linkData, world.dimension().location());
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof LinkingLecternBlockEntity) {
                LinkingLecternBlockEntity lecternTE = (LinkingLecternBlockEntity) tileEntity;
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
