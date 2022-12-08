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
package thefloydman.linkingbooks.client.gui.widget;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.util.Reference;

public class RecipeCarouselWidget extends NestedWidget {

    protected static final ResourceLocation CRAFTING_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/guidebook/crafting.png");

    public long creationTime;
    public long changeTime = 2000L; // 2 seconds
    public int totalVariations = 0;
    Map<Integer, NestedWidget> renderMap = Maps.newHashMap();

    public RecipeCarouselWidget(String id, int x, int y, float z, int width, int height, Component narration,
            Screen parentScreen, float scale, List<List<List<ItemStack>>> recipes) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        for (int i = 0; i < recipes.size(); i++) {
            List<List<ItemStack>> ingredients = recipes.get(i);
            RecipeWidget recipeWidget = new RecipeWidget(this.id + "recipe" + i, this.x, this.y, z + 1.0F, this.width,
                    this.height, Component.literal("Recipe"), parentScreen, 0.5F, ingredients);
            this.addChild(recipeWidget);
            int size = ingredients.stream().max(Comparator.comparing(List::size)).get().size();
            this.totalVariations += size;
            for (int j = 0; j < size; j++) {
                renderMap.put(renderMap.size(), recipeWidget);
            }
        }
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            int generalIndex = Mth.fastFloor((System.currentTimeMillis() - this.creationTime) / this.changeTime)
                    % this.totalVariations;
            NestedWidget recipeWidget = this.renderMap.get(generalIndex);
            if (recipeWidget != null) {
                recipeWidget.render(poseStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public void restore(NestedWidget backup) {
        if (backup instanceof RecipeCarouselWidget) {
            RecipeCarouselWidget old = (RecipeCarouselWidget) backup;
            this.creationTime = old.creationTime;
        }
    }

}
