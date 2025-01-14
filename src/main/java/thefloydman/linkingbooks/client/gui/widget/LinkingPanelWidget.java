/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import thefloydman.linkingbooks.LinkingBooksConfig;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.network.server.LinkMessage;

import javax.annotation.Nonnull;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    public boolean holdingBook = false;
    public LinkData linkData = LinkData.EMPTY;
    public boolean canLink = false;
    public boolean isReltoBook = false;
    DynamicTexture linkingPanelImage = null;
    private ResourceLocation linkingPanelImageResourceLocation;
    private final TextureTarget linkingPanelFramebuffer = new TextureTarget(2, 2, true, false);
    private static final ResourceLocation RELTO_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/reltobook/relto_linking_panel.png");

    public LinkingPanelWidget(String id, int x, int y, float z, int width, int height, Component narration,
                              Screen parentScreen, float scale, boolean holdingBook, boolean isReltoBook, LinkData linkData, boolean canLink,
                              NativeImage linkingPanelImage) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.holdingBook = holdingBook;
        this.linkData = linkData;
        this.isReltoBook = isReltoBook;
        this.canLink = canLink || this.isReltoBook;
        if (linkingPanelImage != null) {
            NativeImage image256 = new NativeImage(256, 256, false);
            linkingPanelImage.copyRect(image256, 0, 0, 0, 0, linkingPanelImage.getWidth(), linkingPanelImage.getHeight(), false, false);
            this.linkingPanelImage = new DynamicTexture(image256);
            this.linkingPanelImageResourceLocation = Minecraft.getInstance().getTextureManager().register("gui_linking_panel_image", this.linkingPanelImage);
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            int panelColor = this.canLink ? new Color(32, 192, 255).getRGB() : new Color(192, 192, 192).getRGB();
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, (int) this.zLevel, panelColor);
            if (this.canLink) {
                if (Reference.isImmersivePortalsLoaded() && LinkingBooksConfig.USE_IP_FOR_LINKING_PANELS.get()) {
                    guiGraphics.pose().translate(0.0F, 0.0F, 1.0F);
                    ImmersivePortalsIntegration.renderGuiPortal(
                            this.linkData,
                            this.linkingPanelFramebuffer,
                            this.minecraft,
                            this.getX(), this.getY(),
                            this.getWidth(), this.getHeight()
                    );
                    this.linkingPanelFramebuffer.clear(true);
                } else if (this.linkingPanelImage != null && this.linkingPanelImage.getPixels() != null) {
                    guiGraphics.blit(
                            this.linkingPanelImageResourceLocation,
                            this.getX(), this.getY(),
                            (int) this.zLevel + 1, 0, 0,
                            this.getWidth(), this.getHeight(),
                            this.linkingPanelImage.getPixels().getWidth(), this.linkingPanelImage.getPixels().getHeight()
                    );
                } else if (this.isReltoBook) {
                    guiGraphics.blit(
                            RELTO_TEXTURE,
                            this.getX(), this.getY(),
                            (int) this.zLevel + 1, 0, 0,
                            this.getWidth(), this.getHeight(),
                            64, 42
                    );
                }
            }
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isInside(mouseX, mouseY)) {
            PacketDistributor.sendToServer(new LinkMessage(this.linkData, this.holdingBook, this.isReltoBook));
            return true;
        }
        return this.onMouseClickChildren(mouseX, mouseY, button);
    }

}
