/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.util.Reference;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> GUIDEBOOK = ITEMS.register(Reference.ItemNames.GUIDEBOOK,
            () -> new GuidebookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BLACK_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BLACK_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BLUE_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BLUE_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BROWN_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BROWN_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CYAN_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.CYAN_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> GRAY_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.GRAY_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> GREEN_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.GREEN_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LIGHT_BLUE_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.LIGHT_BLUE_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LIGHT_GRAY_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.LIGHT_GRAY_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LIME_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.LIME_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> MAGENTA_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.MAGENTA_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> ORANGE_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.ORANGE_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> PINK_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.PINK_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> PURPLE_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.PURPLE_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> RED_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.RED_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> WHITE_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.WHITE_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> YELLOW_BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.YELLOW_BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BLACK_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BLACK_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BLUE_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BLUE_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BROWN_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BROWN_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CYAN_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.CYAN_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> GRAY_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.GRAY_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> GREEN_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.GREEN_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LIGHT_BLUE_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.LIGHT_BLUE_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LIGHT_GRAY_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.LIGHT_GRAY_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LIME_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.LIME_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> MAGENTA_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.MAGENTA_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> ORANGE_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.ORANGE_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> PINK_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.PINK_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> PURPLE_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.PURPLE_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> RED_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.RED_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> WHITE_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.WHITE_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> YELLOW_WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.YELLOW_WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LINKING_LECTERN = ITEMS.register(Reference.BlockNames.LINKING_LECTERN,
            () -> new BlockItem(ModBlocks.LINKING_LECTERN.get(), new Item.Properties()));

    public static final RegistryObject<Item> NARA = ITEMS.register(Reference.BlockNames.NARA,
            () -> new BlockItem(ModBlocks.NARA.get(), new Item.Properties()));

    public static final RegistryObject<Item> LINK_TRANSLATOR = ITEMS.register(Reference.BlockNames.LINK_TRANSLATOR,
            () -> new BlockItem(ModBlocks.LINK_TRANSLATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> MARKER_SWITCH = ITEMS.register(Reference.BlockNames.MARKER_SWITCH,
            () -> new BlockItem(ModBlocks.MARKER_SWITCH.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOOKSHELF_STAIRS = ITEMS.register(Reference.BlockNames.BOOKSHELF_STAIRS,
            () -> new BlockItem(ModBlocks.BOOKSHELF_STAIRS.get(), new Item.Properties()));

    public static final RegistryObject<Item> LINKING_PANEL = ITEMS.register(Reference.ItemNames.LINKING_PANEL,
            () -> new LinkingPanelItem(new Item.Properties()));

    public static final RegistryObject<Item> INK_BUCKET = ITEMS.register(Reference.ItemNames.INK_BUCKET,
            () -> new BucketItem(ModFluids.INK, new Item.Properties()));

}
