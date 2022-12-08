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
package thefloydman.linkingbooks.client.gui.book;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import thefloydman.linkingbooks.client.gui.widget.RecipeCarouselWidget;

public class GuiBookRecipeCarousel extends GuiBookElement<RecipeCarouselWidget> {

    private final List<ResourceLocation> resourceLocations;
    private final RecipeType<?> recipeType;

    public GuiBookRecipeCarousel(RecipeType<?> recipeType, List<ResourceLocation> resourceLocations) {
        super("recipecarousel");
        this.resourceLocations = resourceLocations;
        this.recipeType = recipeType;
    }

    @Override
    public RecipeCarouselWidget getAsWidget(String id, int x, int y, float z, int width, int height,
            Screen parentScreen, float scale, Font font) {

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return null;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        if (recipeManager == null) {
            return null;
        }

        int gridWidth = 107;
        int gridHeight = 62;
        List<List<List<ItemStack>>> recipesList = Lists.newArrayList();
        for (ResourceLocation location : this.resourceLocations) {
            List<List<ItemStack>> singleList = Lists.newArrayList();
            Recipe<?> recipe = recipeManager.byKey(location).orElse(null);
            if (recipe != null && recipe.getType() == this.recipeType) {
                if (this.recipeType == RecipeType.CRAFTING) {
                    NonNullList<Ingredient> ingredients = recipe.getIngredients();
                    for (Ingredient ingredient : ingredients) {
                        singleList.add(Stream.of(ingredient.getItems()).toList());
                    }
                }
                singleList.add(Stream.of(recipe.getResultItem()).toList());
                recipesList.add(singleList);
            }
        }

        return new RecipeCarouselWidget(id, (int) (x + ((width - (gridWidth * scale)) / 2.0F)), y, z, gridWidth,
                gridHeight, Component.literal("Recipe"), parentScreen, scale, recipesList);
    }

}
