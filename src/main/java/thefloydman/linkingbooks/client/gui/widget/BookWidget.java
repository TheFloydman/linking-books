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
import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.util.Reference;

@OnlyIn(Dist.CLIENT)
public class BookWidget extends NestedWidget {

    private static final ResourceLocation COVER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_cover.png");
    private static final ResourceLocation PAPER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_paper.png");
    private List<FormattedPageWidget> pages = Lists.newArrayList();
    private PageChangeWidget previousArrow;
    private PageChangeWidget nextArrow;
    private int currentSpread = 0;
    private int color = new Color(80, 111, 203).getRGB();

    public BookWidget(String id, int x, int y, float zLevel, int width, int height, Component narration, Font font,
            List<List<Object>> pages) {
        super(id, x, y, width, height, narration);
        int marginLeftX = 20;
        int marginRightX = 10;
        int marginY = 16;
        for (int i = 0; i < pages.size(); i++) {
            List<Object> page = pages.get(i);
            int localX = i % 2 == 0 ? x + marginLeftX : x + (width / 2) + marginRightX;
            FormattedPageWidget child = this.addChild(new FormattedPageWidget("guidebook page " + i, localX,
                    y + marginY, (width / 2) - marginLeftX - marginRightX, (height / 2) - (marginY * 2),
                    Component.literal("Page " + i), font, page));
            child.visible = false;
            this.pages.add(child);
        }
        this.previousArrow = this.addChild(new PageChangeWidget("back arrow", this.x + 16,
                this.y + this.getHeight() - 33, Component.literal("Previous Page"), PageChangeWidget.Type.PREVIOUS));
        this.nextArrow = this.addChild(new PageChangeWidget("forward arrow", this.x + this.getWidth() - 18 - 16,
                this.y + this.getHeight() - 33, Component.literal("Next Page"), PageChangeWidget.Type.NEXT));
        previousArrow.addListener(this);
        nextArrow.addListener(this);
        updateVisible();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        matrixStack.pushPose();

        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, COVER_TEXTURE);
        float[] color = new Color(this.color).getRGBColorComponents(null);
        RenderSystem.setShaderColor(Mth.clamp(color[0], 0.1F, 1.0F), Mth.clamp(color[1], 0.1F, 1.0F),
                Mth.clamp(color[2], 0.1F, 1.0F), 1.0F);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.width, this.height);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, PAPER_TEXTURE);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.width, this.height);

        matrixStack.popPose();

        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void previousPage() {
        this.currentSpread--;
        if (this.currentSpread < 0)
            this.currentSpread = 0;
        updateVisible();
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.player.playSound(ModSounds.PAGEFLIP_BACK.get());
    }

    public void nextPage() {
        this.currentSpread++;
        if (this.currentSpread * 2 >= this.pages.size())
            this.currentSpread = Mth.floor(this.pages.size() / 2) - 1;
        updateVisible();
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.player.playSound(ModSounds.PAGEFLIP_FORWARD.get());
    }

    private void updateVisible() {
        for (int i = 0; i < this.pages.size(); i++) {
            if (i == currentSpread * 2 || i == currentSpread * 2 + 1) {
                this.pages.get(i).visible = true;
            } else {
                this.pages.get(i).visible = false;
            }
        }
        this.previousArrow.visible = currentSpread > 0;
        this.nextArrow.visible = currentSpread * 2 + 1 < this.pages.size() - 1;
    }

    @Override
    public void restore(NestedWidget backup) {
        BookWidget old = BookWidget.class.cast(backup);
        if (old != null) {
            this.currentSpread = old.currentSpread;
            this.updateVisible();
        }
    }

}
