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
package thefloydman.linkingbooks.client.resources.guidebook;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

public class GuidebookRecipe {

    public RecipeType type;
    public List<Ingredient> ingredients;
    public List<List<ItemStack>> renderable = Lists.newArrayList();

    public GuidebookRecipe(RecipeType type, List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.type = type;
    }

    public void makeRenderable() {
        List<List<ItemStack>> outputRecipe = Lists.newArrayList();
        for (int i = 0; i < this.ingredients.size(); i++) {
            GuidebookRecipe.Ingredient ingredient = this.ingredients.get(i);
            List<ItemStack> currentIngredients = Lists.newArrayList();
            switch (ingredient.type) {
                case ITEM:
                    ItemStack singleItem = ForgeRegistries.ITEMS.getValue(ingredient.resourceLocation)
                            .getDefaultInstance();
                    singleItem.setCount(ingredient.quantity);
                    currentIngredients.add(singleItem);
                    break;
                case TAG:
                    ITag<Item> tag = ForgeRegistries.ITEMS.tags()
                            .getTag(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ingredient.resourceLocation));
                    for (Item taggedItem : tag) {
                        ItemStack itemStack = taggedItem.getDefaultInstance();
                        itemStack.setCount(ingredient.quantity);
                        currentIngredients.add(itemStack);
                    }
                    break;
                default:
                    currentIngredients.add(ItemStack.EMPTY);
                    break;
            }
            outputRecipe.add(currentIngredients);
        }
        this.renderable = outputRecipe;
    }

    public static Ingredient emptyIngredient() {
        return new Ingredient(Ingredient.IngredientType.EMPTY, new ResourceLocation(""), 1);
    }

    public enum RecipeType {
        CRAFTING
    }

    public static class Ingredient {

        public Ingredient.IngredientType type;
        public ResourceLocation resourceLocation;
        public int quantity;

        public Ingredient(Ingredient.IngredientType type, ResourceLocation resourceLocation, int quantity) {
            this.type = type;
            this.resourceLocation = resourceLocation;
            this.quantity = quantity;
        }

        public enum IngredientType {
            EMPTY,
            ITEM,
            TAG;
        }

    }

}
