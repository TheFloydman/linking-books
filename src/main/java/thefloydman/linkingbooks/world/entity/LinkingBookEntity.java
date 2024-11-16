/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.world.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.world.item.WrittenLinkingBookItem;

public class LinkingBookEntity extends ObjectEntity {

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, Level world) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F);
        if (world.isClientSide()) {
            setViewScale(2.0D);
        }
    }

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, Level world, ItemStack item) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F, item);
        if (world.isClientSide()) {
            setViewScale(2.0D);
        }
    }

    public LinkingBookEntity(Level world) {
        this(ModEntityTypes.LINKING_BOOK.get(), world);
    }

    public LinkingBookEntity(Level world, ItemStack item) {
        this(ModEntityTypes.LINKING_BOOK.get(), world, item);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if (hand == InteractionHand.MAIN_HAND) {
                ItemStack bookStack = this.getItem();
                if (!bookStack.isEmpty()) {
                    if (serverPlayer.isShiftKeyDown()) {
                        serverPlayer.addItem(bookStack);
                        serverPlayer.inventoryMenu.broadcastChanges();
                        this.remove(RemovalReason.DISCARDED);
                        return InteractionResult.SUCCESS;
                    } else {
                        LinkData linkData = bookStack.get(ModDataComponents.LINK_DATA);
                        if (linkData != null) {
                            LinkingUtils.openLinkingBookGui(serverPlayer, false, LinkingUtils.getLinkingBookColor(bookStack, 0),
                                    linkData, serverPlayer.getCommandSenderWorld().dimension().location());
                            return InteractionResult.CONSUME;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

}