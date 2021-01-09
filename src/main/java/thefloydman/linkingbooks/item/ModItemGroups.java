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

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.util.Reference;

public class ModItemGroups {

    public static final ItemGroup LINKING_BOOKS = new ItemGroup(Reference.MOD_ID) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.WRITTEN_LINKING_BOOK.get());
        }

        @Override
        public void fill(NonNullList<ItemStack> items) {
            super.fill(items);
            /*
             * Adds a blank linking book to the Linking Books creative tab for each of the
             * 16 standard dye colors.
             */
            for (DyeColor color : DyeColor.values()) {
                ItemStack stack = ModItems.BLANK_LINKING_BOOK.get().getDefaultInstance();
                IColorCapability cap = stack.getCapability(ColorCapability.COLOR).orElse(null);
                if (cap != null) {
                    cap.setColor(color.getColorValue());
                    items.add(stack);
                }
            }
        }
    };

}
