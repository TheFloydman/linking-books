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

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.util.Reference;

public class RecipeWidget extends NestedWidget {

    protected static final ResourceLocation CRAFTING_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/guidebook/crafting.png");

    public RecipeWidget(String id, int x, int y, float z, int width, int height, Component narration,
            Screen parentScreen, float scale, List<List<ItemStack>> ingredients) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        for (int i = 0; i < ingredients.size(); i++) {
            int gridX = i == ingredients.size() - 1 ? 91 : (int) (3.0F + ((i % 3.0F) * 20.0F));
            int gridY = i == ingredients.size() - 1 ? 23 : (int) (3.0F + (Mth.fastFloor(i / 3.0F) * 20.0F));
            this.addChild(new ItemStackWidget(id + "ingr" + i, (int) (this.x + gridX * this.scale),
                    (int) (this.y + gridY * this.scale), z++, 16, 16, Component.literal("Ingredient"), parentScreen,
                    scale, ingredients.get(i)));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            poseStack.pushPose();
            poseStack.scale(this.scale, this.scale, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                    DestFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, CRAFTING_TEXTURE);
            blit(poseStack, (int) (this.x / this.scale), (int) (this.y / this.scale), 1, 0, 0, this.width, this.height,
                    256, 256);
            this.renderChildren(poseStack, mouseX, mouseY, partialTicks);
            poseStack.popPose();
        }
    }

}
