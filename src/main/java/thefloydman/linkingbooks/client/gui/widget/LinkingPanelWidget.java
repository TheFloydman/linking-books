/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */
package thefloydman.linkingbooks.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.network.LinkMessage;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    public boolean holdingBook = false;
    public LinkData linkData = LinkData.EMPTY;
    public boolean canLink = false;
    DynamicTexture linkingPanelImage = null;
    private ResourceLocation guiLinkingPanelImageResourceLocation;

    public LinkingPanelWidget(String id, int x, int y, float z, int width, int height, Component narration,
                              Screen parentScreen, float scale, boolean holdingBook, LinkData linkData, boolean canLink,
                              NativeImage linkingPanelImage) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.holdingBook = holdingBook;
        this.linkData = linkData;
        this.canLink = canLink;
        if (linkingPanelImage != null) {
            NativeImage image256 = new NativeImage(256, 256, false);
            for (int textureY = 0; textureY < linkingPanelImage.getHeight(); textureY++) {
                for (int textureX = 0; textureX < linkingPanelImage.getWidth(); textureX++) {
                    image256.setPixelRGBA(textureX, textureY, linkingPanelImage.getPixelRGBA(textureX, textureY));
                }
            }
            this.linkingPanelImage = new DynamicTexture(image256);
            guiLinkingPanelImageResourceLocation = Minecraft.getInstance().getTextureManager().register("gui_linking_panel_image", this.linkingPanelImage);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            int panelColor = this.canLink ? new Color(32, 192, 255).getRGB() : new Color(192, 192, 192).getRGB();
            this.zFill(guiGraphics, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height,
                    panelColor);

            if (this.canLink) {
                if (this.linkingPanelImage != null) {
                    guiGraphics.pose().pushPose();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    guiGraphics.blit(this.guiLinkingPanelImageResourceLocation, this.getX(), this.getY(), 150, 0, 0, this.linkingPanelImage.getPixels().getWidth(),
                            this.linkingPanelImage.getPixels().getHeight(), this.linkingPanelImage.getPixels().getWidth(), this.linkingPanelImage.getPixels().getHeight());
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isInside(mouseX, mouseY)) {
            PacketDistributor.sendToServer(new LinkMessage(this.linkData, this.holdingBook));
            return true;
        }
        return this.onMouseClickChildren(mouseX, mouseY, button);
    }

}
