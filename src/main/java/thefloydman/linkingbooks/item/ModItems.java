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

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.block.ModBlocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MODID);

    public static final DeferredItem<Item> GUIDEBOOK = ITEMS.register(Reference.ItemNames.GUIDEBOOK,
            () -> new GuidebookItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> LINKING_PANEL = ITEMS.register(Reference.ItemNames.LINKING_PANEL,
            () -> new LinkingPanelItem(new Item.Properties()));

    public static final DeferredItem<Item> BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(
                    new Item.Properties()
                            .stacksTo(16)
                            .component(DataComponents.DYED_COLOR, new DyedItemColor(
                                    new Color(181, 134, 83).getRGB(), false))));

    public static final DeferredItem<Item> WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(DataComponents.DYED_COLOR,
                                    new DyedItemColor(
                                            new Color(181, 134, 83).getRGB(),
                                            false))
                            .component(ModDataComponents.LINK_DATA,
                                    new LinkData(ResourceLocation.parse("minecraft:overworld"),
                                            Reference.server == null ? BlockPos.ZERO : Reference.server.overworld().getSharedSpawnPos(),
                                            0.0F,
                                            UUID.randomUUID(),
                                            new ArrayList<>()))));

    // Block items

    public static final DeferredItem<Item> BOOKSHELF_STAIRS = ITEMS.register(Reference.BlockNames.BOOKSHELF_STAIRS,
            () -> new BlockItem(ModBlocks.BOOKSHELF_STAIRS.get(), new Item.Properties()));

    public static final DeferredItem<Item> LINKING_LECTERN = ITEMS.register(Reference.BlockNames.LINKING_LECTERN,
            () -> new BlockItem(ModBlocks.LINKING_LECTERN.get(), new Item.Properties()));

    public static final DeferredItem<Item> NARA = ITEMS.register(Reference.BlockNames.NARA,
            () -> new BlockItem(ModBlocks.NARA.get(), new Item.Properties()));

    public static final DeferredItem<Item> LINK_TRANSLATOR = ITEMS.register(Reference.BlockNames.LINK_TRANSLATOR,
            () -> new BlockItem(ModBlocks.LINK_TRANSLATOR.get(), new Item.Properties()));

    public static final DeferredItem<Item> MARKER_SWITCH = ITEMS.register(Reference.BlockNames.MARKER_SWITCH,
            () -> new BlockItem(ModBlocks.MARKER_SWITCH.get(), new Item.Properties()));

}