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

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.Reference;

public class ItemGroups {

    public static final CreativeModeTab LINKING_BOOKS = new CreativeModeTab(Reference.MOD_ID) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return ModItems.GREEN_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.add(ModItems.GUIDEBOOK.get().getDefaultInstance());
            items.add(ModItems.BLACK_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.BLUE_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.BROWN_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.CYAN_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.GRAY_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.GREEN_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.LIGHT_BLUE_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.LIGHT_GRAY_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.LIME_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.MAGENTA_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.ORANGE_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.PINK_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.PURPLE_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.RED_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.WHITE_BLANK_LINKING_BOOK.get().getDefaultInstance());
            items.add(ModItems.YELLOW_BLANK_LINKING_BOOK.get().getDefaultInstance());
        }
    };

}
