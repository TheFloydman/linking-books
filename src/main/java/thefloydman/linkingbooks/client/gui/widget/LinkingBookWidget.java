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

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class LinkingBookWidget extends NestedWidget {

    private static final ResourceLocation COVER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_cover.png");
    private static final ResourceLocation PAPER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_paper.png");

    public int color = DyeColor.GREEN.getFireworkColor();

    public LinkingBookWidget(String id, int x, int y, float z, int width, int height, Component narration,
                             Screen parentScreen, float scale, boolean holdingBook, int color, LinkData linkData, boolean canLink,
                             NativeImage linkingPanelImage) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.color = color;
        NestedWidget linkingPanel = this.addChild(new LinkingPanelWidget("linking panel", this.getX() + 155,
                this.getY() + 41, z + 1.0F, 64, 42, Component.literal("Linking Panel"), parentScreen, this.scale,
                holdingBook, linkData, canLink, linkingPanelImage));
        for (GuiEventListener listener : this.listeners) {
            linkingPanel.addListener(listener);
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            float[] color = new Color(this.color).getRGBColorComponents(null);
            RenderSystem.setShaderColor(Mth.clamp(color[0], 0.1F, 1.0F), Mth.clamp(color[1], 0.1F, 1.0F),
                    Mth.clamp(color[2], 0.1F, 1.0F), 1.0F);
            guiGraphics.blit(COVER_TEXTURE, this.getX(), this.getY(), 0, 0, this.width, this.height);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(PAPER_TEXTURE, this.getX(), this.getY(), 0, 0, this.width, this.height);
            this.renderChildren(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.pose().popPose();
        }
    }

}
