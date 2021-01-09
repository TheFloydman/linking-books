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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkingBookCapabilityProvider;

public abstract class LinkingBookItem extends Item {

    public LinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new LinkingBookCapabilityProvider();
    }

    /**
     * Used to color item texture. Any tintIndex besides 0 will return -1.
     */
    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
        if (color != null) {
            return color.getColor();
        }
        return DyeColor.GREEN.getColorValue();
    }

    /*
     * These two methods help ensure that itemstacks with capabilities in the
     * creative menu and crafting table result keep their capabilities when placed
     * in the player's inventories.
     * 
     * TODO: Remove when this issue is fixed:
     * https://github.com/brandon3055/Draconic-Evolution/blob/
     * 4af607da1f7eb144cd6fed5708611a86356f5c66/src/main/java/com/brandon3055/
     * draconicevolution/items/equipment/IModularItem.java#L219-L227
     */

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
        if (color != null) {
            CompoundNBT tag = color.writeToShareTag(nbt);
            return tag;
        }
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        stack.setTag(nbt);
        if (nbt != null) {
            IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
            if (color != null) {
                color.readFromShareTag(nbt);
            }
        }
    }

}
