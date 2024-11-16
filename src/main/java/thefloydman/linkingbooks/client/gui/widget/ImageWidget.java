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

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageWidget extends NestedWidget {

    public ResourceLocation resourceLocation;
    public int sourceWidth;
    public int sourceHeight;
    public int sourceX;
    public int sourceY;

    public ImageWidget(String id, int x, int y, float z, int width, int height, Component narration,
                       Screen parentScreen, float scale, ResourceLocation resourceLocation, int sourceWidth, int sourceHeight,
                       int sourceX, int sourceY) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.resourceLocation = resourceLocation;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                    DestFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), 1, this.sourceX, this.sourceY, (int) (this.width * this.scale),
                    (int) (this.height * this.scale), (int) (this.width * this.scale),
                    (int) (this.height * this.scale));
            guiGraphics.pose().popPose();
        }
    }

}
