/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
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
package thefloydman.linkingbooks.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RecipeWidget extends NestedWidget {

    protected static final ResourceLocation CRAFTING_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/guidebook/crafting.png");

    public RecipeWidget(String id, int x, int y, float z, int width, int height, Component narration,
                        Screen parentScreen, float scale, List<List<ItemStack>> ingredients) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        for (int i = 0; i < ingredients.size(); i++) {
            int gridX = i == ingredients.size() - 1 ? 91 : (int) (3.0F + ((i % 3.0F) * 20.0F));
            int gridY = i == ingredients.size() - 1 ? 23 : (int) (3.0F + (Mth.floor(i / 3.0F) * 20.0F));
            this.addChild(new ItemStackWidget(id + "ingr" + i, (int) (this.getX() + gridX * this.scale),
                    (int) (this.getY() + gridY * this.scale), z++, 16, 16, Component.literal("Ingredient"),
                    parentScreen, scale, ingredients.get(i)));
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(this.scale, this.scale, 1.0F);
            guiGraphics.blit(CRAFTING_TEXTURE, (int) (this.getX() / this.scale), (int) (this.getY() / this.scale), 1, 0, 0, this.width,
                    this.height, 256, 256);
            this.renderChildren(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.pose().popPose();
        }
    }

}
