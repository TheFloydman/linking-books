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
package thefloydman.linkingbooks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkingBookEntity extends ObjectEntity {

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, World world) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F);
        if (world.isClientSide()) {
            setViewScale(2.0D);
        }
    }

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, World world, ItemStack item) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F, item);
        if (world.isClientSide()) {
            setViewScale(2.0D);
        }
    }

    public LinkingBookEntity(World world) {
        this(ModEntityTypes.LINKING_BOOK.get(), world);
    }

    public LinkingBookEntity(World world, ItemStack item) {
        this(ModEntityTypes.LINKING_BOOK.get(), world, item);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            if (hand == Hand.MAIN_HAND) {
                ItemStack bookStack = this.getItem();
                if (!bookStack.isEmpty()) {
                    if (serverPlayer.isShiftKeyDown()) {
                        serverPlayer.addItem(bookStack);
                        serverPlayer.inventoryMenu.broadcastChanges();
                        this.remove();
                        return ActionResultType.SUCCESS;
                    } else {
                        ILinkData linkData = bookStack.getCapability(LinkData.LINK_DATA).orElse(null);
                        IColorCapability color = bookStack.getCapability(ColorCapability.COLOR).orElse(null);
                        if (linkData != null && color != null) {
                            LinkingUtils.openLinkingBookGui(serverPlayer, false, color.getColor(), linkData,
                                    serverPlayer.getCommandSenderWorld().dimension().location());
                            return ActionResultType.CONSUME;
                        }
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

}
