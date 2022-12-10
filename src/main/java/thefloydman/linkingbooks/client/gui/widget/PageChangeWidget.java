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
package thefloydman.linkingbooks.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PageChangeWidget extends NestedWidget {

    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("minecraft:textures/gui/book.png");
    private final Type type;

    public enum Type {
        PREVIOUS(3, 207, 26, 207, 18, 10),
        NEXT(3, 194, 26, 194, 18, 10);

        public final int xUp;
        public final int yUp;
        public final int xHover;
        public final int yHover;
        public final int width;
        public final int height;

        Type(int xUp, int yUp, int xHover, int yHover, int width, int height) {
            this.xUp = xUp;
            this.yUp = yUp;
            this.xHover = xHover;
            this.yHover = yHover;
            this.width = width;
            this.height = height;
        }
    }

    public PageChangeWidget(String id, int x, int y, float z, Component narration, Screen parentScreen, float scale,
            Type type) {
        super(id, x, y, z, type.width, type.height, narration, parentScreen, scale);
        this.type = type;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            poseStack.pushPose();
            if (this.isInside(mouseX, mouseY)) {
                RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                        DestFactor.ZERO);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, ARROW_TEXTURE);
                blit(poseStack, this.getX(), this.getY(), 1, this.type.xHover, this.type.yHover, this.type.width,
                        this.type.height, 256, 256);
            } else {
                RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                        DestFactor.ZERO);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 1.0F);
                RenderSystem.setShaderTexture(0, ARROW_TEXTURE);
                blit(poseStack, this.getX(), this.getY(), 1, this.type.xUp, this.type.yUp, this.type.width,
                        this.type.height, 256, 256);
            }
            poseStack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!this.isInside(x, y))
            return false;
        for (GuiEventListener listener : this.listeners) {
            if (listener instanceof BookWidget) {
                BookWidget book = (BookWidget) listener;
                switch (this.type) {
                    case PREVIOUS:
                        book.previousPage();
                        break;
                    default:
                        book.nextPage();
                        break;
                }
            }
        }
        return true;
    }

}
