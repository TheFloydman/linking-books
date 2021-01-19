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
import net.minecraft.client.shader.Framebuffer;
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
    private static Framebuffer frameBuffer = new Framebuffer(64, 42, true, true);
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
             * TODO: Enable Immersive Portals support when chunkloading is working if
             * (ModList.get().isLoaded("immersive_portals")) { Matrix4f cameraTransformation
             * = new Matrix4f(); cameraTransformation.setIdentity();
             * cameraTransformation.mul(Vector3f.YP.rotationDegrees(this.linkData.
             * getRotation() + 180.0F)); WorldRenderInfo worldRenderInfo = new
             * WorldRenderInfo( ClientWorldLoader
             * .getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
             * this.linkData.getDimension())), new
             * Vector3d(this.linkData.getPosition().getX() + 0.5D,
             * this.linkData.getPosition().getY() + 1.5D, this.linkData.getPosition().getZ()
             * + 0.5D), cameraTransformation, null,
             * ModConfig.COMMON.linkingPanelChunkRenderDistance.get(), true);
             * GuiPortalRendering.submitNextFrameRendering(worldRenderInfo, frameBuffer);
             * MyRenderHelper.drawFramebuffer(frameBuffer, false, false, this.x * (float)
             * client.getMainWindow().getGuiScaleFactor(), (this.x + this.width) * (float)
             * client.getMainWindow().getGuiScaleFactor(), this.y * (float)
             * client.getMainWindow().getGuiScaleFactor(), (this.y + this.height) * (float)
             * client.getMainWindow().getGuiScaleFactor()); } else
             */if (this.linkingPanelImage != null) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.linkingPanelImage.bindTexture();
                this.blit(matrixStack, this.x, this.y, 0, 0, this.linkingPanelImage.getTextureData().getWidth(),
                        this.linkingPanelImage.getTextureData().getHeight());
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
