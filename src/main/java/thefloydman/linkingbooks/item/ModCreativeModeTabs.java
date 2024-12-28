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

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.Reference;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MODID);

    private static final int[] BOOK_COLORS = {
            /* black */      new Color(0, 0, 0, 255).getRGB(),
            /* blue */       new Color(16, 95, 232, 255).getRGB(),
            /* brown */      new Color(16, 95, 232, 255).getRGB(),
            /* cyan */       new Color(94, 202, 235, 255).getRGB(),
            /* gray */       new Color(115, 115, 115, 255).getRGB(),
            /* green */      new Color(0, 138, 7, 255).getRGB(),
            /* light blue */ new Color(166, 213, 255, 255).getRGB(),
            /* light gray */ new Color(189, 189, 189, 255).getRGB(),
            /* lime */       new Color(128, 232, 133, 255).getRGB(),
            /* magenta */    new Color(207, 54, 102, 255).getRGB(),
            /* orange */     new Color(237, 162, 12, 255).getRGB(),
            /* pink */       new Color(247, 168, 212, 255).getRGB(),
            /* purple */     new Color(134, 21, 232, 255).getRGB(),
            /* red */        new Color(209, 15, 15, 255).getRGB(),
            /* white */      new Color(250, 250, 250, 255).getRGB(),
            /* yellow */     new Color(255, 253, 145, 255).getRGB()
    };

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register(Reference.CreativeModeTabNames.MAIN, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.linkingbooks")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ModItems.BOOKSHELF_STAIRS.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.acceptAll(List.of(
                ModItems.GUIDEBOOK.toStack(),
                ModItems.RELTO_BOOK.toStack(),
                ModItems.LINKING_LECTERN.toStack(),
                ModItems.NARA.toStack(),
                ModItems.LINK_TRANSLATOR.toStack(),
                ModItems.MARKER_SWITCH.toStack(),
                ModItems.LINKING_PANEL.toStack(),
                ModItems.BOOKSHELF_STAIRS.toStack(),
                ModItems.BLANK_LINKING_BOOK.toStack()
        ));
        output.acceptAll(Arrays.stream(BOOK_COLORS).mapToObj(color -> {
            ItemStack linkingBook = ModItems.BLANK_LINKING_BOOK.get().getDefaultInstance();
            linkingBook.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
            return linkingBook;
        }).collect(Collectors.toSet()));
    }).build());

}