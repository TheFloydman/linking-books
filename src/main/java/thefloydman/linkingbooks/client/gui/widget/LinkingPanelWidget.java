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

import java.awt.Color;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.network.packets.LinkMessage;
import thefloydman.linkingbooks.util.ImageUtils;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    public boolean holdingBook = false;
    public ILinkData linkData = new LinkData();
    public boolean canLink = false;
    DynamicTexture linkingPanelImage = null;
    Minecraft client = Minecraft.getInstance();

    public LinkingPanelWidget(String id, int x, int y, float z, int width, int height, Component narration,
            Screen parentScreen, float scale, boolean holdingBook, ILinkData linkData, boolean canLink,
            CompoundTag linkingPanelImageTag) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.holdingBook = holdingBook;
        this.linkData = linkData;
        this.canLink = canLink;
        if (linkingPanelImageTag != null && !linkingPanelImageTag.isEmpty()) {
            NativeImage linkingPanelImage = ImageUtils.imageFromNBT(linkingPanelImageTag);
            NativeImage image256 = new NativeImage(256, 256, false);
            for (int textureY = 0; textureY < linkingPanelImage.getHeight(); textureY++) {
                for (int textureX = 0; textureX < linkingPanelImage.getWidth(); textureX++) {
                    image256.setPixelRGBA(textureX, textureY, linkingPanelImage.getPixelRGBA(textureX, textureY));
                }
            }
            this.linkingPanelImage = new DynamicTexture(image256);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            int panelColor = this.canLink ? new Color(32, 192, 255).getRGB() : new Color(192, 192, 192).getRGB();
            this.zFill(poseStack, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height,
                    panelColor);

            if (this.canLink) {
                if (this.linkingPanelImage != null) {
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, this.linkingPanelImage.getId());
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    this.blit(poseStack, this.getX(), this.getY(), 0, 0, this.linkingPanelImage.getPixels().getWidth(),
                            this.linkingPanelImage.getPixels().getHeight());
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isInside(mouseX, mouseY)) {
            ModNetworkHandler.sendToServer(new LinkMessage(this.holdingBook, this.linkData));
            return true;
        }
        return this.onMouseClickChildren(mouseX, mouseY, button);
    }

}
