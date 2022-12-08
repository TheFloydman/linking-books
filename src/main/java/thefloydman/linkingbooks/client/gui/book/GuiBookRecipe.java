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
package thefloydman.linkingbooks.client.gui.book;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import thefloydman.linkingbooks.client.gui.widget.RecipeWidget;

public class GuiBookRecipe extends GuiBookElement<RecipeWidget> {

    private final ResourceLocation resourceLocation;
    private final RecipeType<?> recipeType;

    public GuiBookRecipe(RecipeType<?> recipeType, ResourceLocation resourceLocation) {
        super("recipe");
        this.resourceLocation = resourceLocation;
        this.recipeType = recipeType;
    }

    @Override
    public RecipeWidget getAsWidget(String id, int x, int y, float z, int width, int height, Screen parentScreen,
            float scale, Font font) {

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
        List<List<ItemStack>> recipeList = Lists.newArrayList();
        Recipe<?> recipe = recipeManager.byKey(this.resourceLocation).orElse(null);
        if (recipe != null && recipe.getType() == this.recipeType) {
            if (this.recipeType == RecipeType.CRAFTING) {
                NonNullList<Ingredient> ingredients = recipe.getIngredients();
                for (Ingredient ingredient : ingredients) {
                    recipeList.add(Stream.of(ingredient.getItems()).toList());
                }
                recipeList.add(Stream.of(recipe.getResultItem()).toList());
            }
        }
        return (new RecipeWidget(id, (int) (x + ((width - (gridWidth * scale)) / 2.0F)), y, z, gridWidth, gridHeight,
                new TextComponent("Recipe"), parentScreen, scale, recipeList));

    }

}
