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

package thefloydman.linkingbooks.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.item.ReltoBookItem;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.linking.LinkingUtils;

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
                        } else if (bookStack.getItem() instanceof ReltoBookItem) {
                            CustomData customData = bookStack.get(DataComponents.CUSTOM_DATA);
                            if (customData != null) {
                                Tag ownerTag = customData.copyTag().get("owner");
                                if (ownerTag != null && ownerTag.getType() == TagTypes.getType(Tag.TAG_INT_ARRAY)) {
                                    LinkingUtils.openReltoBookGui(serverPlayer, customData.copyTag().getUUID("owner"));
                                }
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

}