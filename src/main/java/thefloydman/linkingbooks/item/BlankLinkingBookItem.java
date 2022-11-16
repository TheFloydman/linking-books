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
package thefloydman.linkingbooks.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.util.LinkingUtils;

public class BlankLinkingBookItem extends LinkingBookItem {

    public BlankLinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && !heldStack.isEmpty()) {
            ItemStack writtenBook = LinkingUtils.createWrittenLinkingBook(player, heldStack);
            player.addItem(writtenBook);
            heldStack.shrink(1);
            return InteractionResultHolder.pass(heldStack);
        }
        return InteractionResultHolder.pass(heldStack);
    }

}
