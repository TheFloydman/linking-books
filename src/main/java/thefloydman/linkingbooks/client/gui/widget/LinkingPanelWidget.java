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

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
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
    public ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
    public boolean canLink = false;
    DynamicTexture linkingPanelImage = null;
    Minecraft client = Minecraft.getInstance();

    public LinkingPanelWidget(int x, int y, float zLevel, int width, int height, ITextComponent narration,
            boolean holdingBook, ILinkData linkData, boolean canLink, CompoundNBT linkingPanelImageTag) {
        super(x, y, width, height, narration);
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        int panelColor = this.canLink ? new Color(32, 192, 255).getRGB() : new Color(192, 192, 192).getRGB();
        this.zFill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, panelColor);

        if (this.canLink) {
            /*
             * if (Reference.isImmersivePortalsLoaded() &&
             * ModConfig.COMMON.useImmersivePortalsForLinkingPanels.get() == true) {
             * ImmersivePortalsIntegration.renderGuiPortal(this.linkData, this.frameBuffer,
             * this.client, matrixStack, this.x, this.y, this.width, this.height); } else
             */if (this.linkingPanelImage != null) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.linkingPanelImage.bind();
                this.blit(matrixStack, this.x, this.y, 0, 0, this.linkingPanelImage.getPixels().getWidth(),
                        this.linkingPanelImage.getPixels().getHeight());
            }
        }

        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
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
