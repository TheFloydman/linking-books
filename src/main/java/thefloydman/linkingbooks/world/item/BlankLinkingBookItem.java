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

package thefloydman.linkingbooks.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thefloydman.linkingbooks.util.LinkingUtils;

public class BlankLinkingBookItem extends Item {

    public BlankLinkingBookItem(Properties properties) {
        super(properties);
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && !heldStack.isEmpty()) {
            ItemStack writtenBook = LinkingUtils.createWrittenLinkingBook(player, heldStack);
            if (heldStack.getCount() > 1) {
                player.addItem(writtenBook);
                heldStack.shrink(1);
            } else {
                return InteractionResultHolder.pass(writtenBook);
            }
        }
        return InteractionResultHolder.pass(heldStack);
    }


}