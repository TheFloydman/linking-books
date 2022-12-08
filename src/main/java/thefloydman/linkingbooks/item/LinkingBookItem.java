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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ModCapabilities;
import thefloydman.linkingbooks.capability.LinkingBookCapabilityProvider;
import thefloydman.linkingbooks.util.Reference;

public abstract class LinkingBookItem extends Item {

    public LinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new LinkingBookCapabilityProvider();
    }

    /**
     * Used to color item texture. Any tintIndex besides 0 will return -1.
     */
    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        String itemName = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath().toString();
        if (itemName.equals(Reference.ItemNames.BLACK_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.BLACK_WRITTEN_LINKING_BOOK)) {
            return DyeColor.BLACK.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.BLUE_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.BLUE_WRITTEN_LINKING_BOOK)) {
            return DyeColor.BLUE.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.BROWN_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.BROWN_WRITTEN_LINKING_BOOK)) {
            return DyeColor.BROWN.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.CYAN_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.CYAN_WRITTEN_LINKING_BOOK)) {
            return DyeColor.CYAN.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.GRAY_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.GRAY_WRITTEN_LINKING_BOOK)) {
            return DyeColor.GRAY.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.LIGHT_BLUE_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.LIGHT_BLUE_WRITTEN_LINKING_BOOK)) {
            return DyeColor.LIGHT_BLUE.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.LIGHT_GRAY_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.LIGHT_GRAY_WRITTEN_LINKING_BOOK)) {
            return DyeColor.LIGHT_GRAY.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.LIME_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.LIME_WRITTEN_LINKING_BOOK)) {
            return DyeColor.LIME.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.MAGENTA_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.MAGENTA_WRITTEN_LINKING_BOOK)) {
            return DyeColor.MAGENTA.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.ORANGE_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.ORANGE_WRITTEN_LINKING_BOOK)) {
            return DyeColor.ORANGE.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.PINK_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.PINK_WRITTEN_LINKING_BOOK)) {
            return DyeColor.PINK.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.PURPLE_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.PURPLE_WRITTEN_LINKING_BOOK)) {
            return DyeColor.PURPLE.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.RED_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.RED_WRITTEN_LINKING_BOOK)) {
            return DyeColor.RED.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.WHITE_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.WHITE_WRITTEN_LINKING_BOOK)) {
            return DyeColor.WHITE.getFireworkColor();
        } else if (itemName.equals(Reference.ItemNames.YELLOW_BLANK_LINKING_BOOK)
                || itemName.equals(Reference.ItemNames.YELLOW_WRITTEN_LINKING_BOOK)) {
            return DyeColor.YELLOW.getFireworkColor();
        }
        return DyeColor.GREEN.getFireworkColor();
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        ILinkData linkData = stack.getCapability(ModCapabilities.LINK_DATA).orElse(null);
        if (linkData != null) {
            CompoundTag tag = linkData.writeToShareTag(nbt);
            return tag;
        }
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        stack.setTag(nbt);
        if (nbt != null) {
            ILinkData linkData = stack.getCapability(ModCapabilities.LINK_DATA).orElse(null);
            if (linkData != null) {
                linkData.readFromShareTag(nbt);
            }
        }
    }

}
