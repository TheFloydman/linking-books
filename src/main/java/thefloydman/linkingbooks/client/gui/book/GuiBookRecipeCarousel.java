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
package thefloydman.linkingbooks.client.gui.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import thefloydman.linkingbooks.client.gui.widget.RecipeCarouselWidget;

import java.util.List;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
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

        int gridWidth = 107;
        int gridHeight = 62;
        List<List<List<ItemStack>>> recipesList = Lists.newArrayList();
        for (ResourceLocation location : this.resourceLocations) {
            List<List<ItemStack>> singleList = Lists.newArrayList();
            RecipeHolder<?> recipeHolder = recipeManager.byKey(location).orElse(null);
            Recipe<?> recipe = null;
            if (recipeHolder != null) {
                recipe = recipeHolder.value();
            }
            if (recipe != null && recipe.getType() == this.recipeType) {
                if (this.recipeType == RecipeType.CRAFTING) {
                    NonNullList<Ingredient> ingredients = recipe.getIngredients();
                    for (Ingredient ingredient : ingredients) {
                        singleList.add(Stream.of(ingredient.getItems()).toList());
                    }
                }
                singleList.add(Stream.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())).toList());
                recipesList.add(singleList);
            }
        }

        return new RecipeCarouselWidget(id, (int) (x + ((width - (gridWidth * scale)) / 2.0F)), y, z, gridWidth,
                gridHeight, Component.literal("Recipe"), parentScreen, scale, recipesList);
    }

}
