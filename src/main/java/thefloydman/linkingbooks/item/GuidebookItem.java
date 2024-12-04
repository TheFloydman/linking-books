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

package thefloydman.linkingbooks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.menutype.GuidebookMenuType;

import javax.annotation.Nonnull;
import java.util.List;

public class GuidebookItem extends Item {

    public GuidebookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && player instanceof ServerPlayer) {
            player.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> new GuidebookMenuType(id, playerInventory), Component.literal("")));
        }
        return InteractionResultHolder.pass(heldStack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext tooltipContext, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, tooltipContext, tooltip, flag);
        tooltip.add(
                Component.literal("§9§o" + Component.translatable("item.linkingbooks.guidebook.subtitle").getString()));
    }

}