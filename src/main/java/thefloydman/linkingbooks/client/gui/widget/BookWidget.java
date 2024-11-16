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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.util.Reference;

import java.awt.*;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BookWidget extends NestedWidget {

    private static final ResourceLocation COVER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_cover.png");
    private static final ResourceLocation PAPER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_paper.png");
    private final List<GuiBookPageWidget> pages = Lists.newArrayList();
    private final PageChangeWidget previousArrow;
    private final PageChangeWidget nextArrow;
    private int currentSpread = 0;
    private final int color;

    public BookWidget(String id, int x, int y, float z, int width, int height, int color, Component narration, Screen parentScreen,
                      float scale, Font font, List<List<Object>> pages) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.color = color;
        int marginLeftX = 20;
        int marginRightX = 10;
        int marginY = 14;
        float iteratedZ = z;
        for (int i = 0; i < pages.size(); i++) {
            List<Object> page = pages.get(i);
            int localX = i % 2 == 0 ? x + marginLeftX : x + (width / 2) + marginRightX;
            GuiBookPageWidget child = this.addChild(new GuiBookPageWidget("guidebook page " + i, localX, y + marginY,
                    iteratedZ++, (width / 2) - marginLeftX - marginRightX, (height / 2) - (marginY * 2),
                    Component.literal("Page " + i), parentScreen, 1.0F, font, page));
            child.setVisible(false);
            this.pages.add(child);
        }
        iteratedZ += 100.0F;
        this.previousArrow = this.addChild(
                new PageChangeWidget("back arrow", this.getX() + 12, this.getY() + this.getHeight() - 21, iteratedZ++,
                        Component.literal("Previous Page"), parentScreen, 1.0F, PageChangeWidget.Type.BACKWARD));
        this.nextArrow = this.addChild(new PageChangeWidget("forward arrow", this.getX() + this.getWidth() - 18 - 18,
                this.getY() + this.getHeight() - 21, iteratedZ++, Component.literal("Next Page"), parentScreen, 1.0F,
                PageChangeWidget.Type.FORWARD));
        previousArrow.addListener(this);
        nextArrow.addListener(this);
        updateVisible();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            guiGraphics.pose().pushPose();
            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                    DestFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

    public void previousPage() {
        this.currentSpread--;
        if (this.currentSpread < 0)
            this.currentSpread = 0;
        updateVisible();
        if (this.minecraft != null && this.minecraft.player != null) {
            minecraft.player.playSound(ModSounds.PAGEFLIP_BACK.get());
        }
    }

    public void nextPage() {
        this.currentSpread++;
        if (this.currentSpread * 2 >= this.pages.size())
            this.currentSpread = Mth.floor((float) this.pages.size() / 2.0F) - 1;
        updateVisible();
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(ModSounds.PAGEFLIP_FORWARD.get());
        }
    }

    private void updateVisible() {
        for (int i = 0; i < this.pages.size(); i++) {
            this.pages.get(i).setVisible(i == currentSpread * 2 || i == currentSpread * 2 + 1);
        }
        this.previousArrow.setVisible(currentSpread > 0);
        this.nextArrow.setVisible(currentSpread * 2 + 1 < this.pages.size() - 1);
    }

    @Override
    public void restore(NestedWidget backup) {
        BookWidget old = (BookWidget) backup;
        if (old != null) {
            this.currentSpread = old.currentSpread;
            this.updateVisible();
        }
    }

}
