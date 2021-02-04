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

import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.fluid.ModFluids;
import thefloydman.linkingbooks.util.Reference;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> BLANK_LINKING_BOOK = ITEMS.register(Reference.ItemNames.BLANK_LINKNG_BOOK,
            () -> new BlankLinkingBookItem(new Item.Properties().maxStackSize(16)));

    public static final RegistryObject<Item> WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.WRITTEN_LINKNG_BOOK,
            () -> new WrittenLinkingBookItem(new Item.Properties().maxStackSize(1)));

    public static final RegistryObject<Item> LINKING_LECTERN = ITEMS.register(Reference.BlockNames.LINKING_LECTERN,
            () -> new BlockItem(ModBlocks.LINKING_LECTERN.get(),
                    new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

    public static final RegistryObject<Item> NARA = ITEMS.register(Reference.BlockNames.NARA,
            () -> new BlockItem(ModBlocks.NARA.get(), new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

    public static final RegistryObject<Item> LINK_TRANSLATOR = ITEMS.register(Reference.BlockNames.LINK_TRANSLATOR,
            () -> new BlockItem(ModBlocks.LINK_TRANSLATOR.get(),
                    new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

    public static final RegistryObject<Item> MARKER_SWITCH = ITEMS.register(Reference.BlockNames.MARKER_SWITCH,
            () -> new BlockItem(ModBlocks.MARKER_SWITCH.get(),
                    new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

    public static final RegistryObject<Item> LINKING_PANEL = ITEMS.register(Reference.ItemNames.LINKING_PANEL,
            () -> new LinkingPanelItem(new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

    public static final RegistryObject<Item> INK_BUCKET = ITEMS.register(Reference.ItemNames.INK_BUCKET,
            () -> new BucketItem(ModFluids.INK, new Item.Properties().group(ModItemGroups.LINKING_BOOKS)));

}
