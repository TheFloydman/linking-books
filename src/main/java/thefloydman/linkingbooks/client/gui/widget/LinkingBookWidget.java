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

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.util.Reference;

@OnlyIn(Dist.CLIENT)
public class LinkingBookWidget extends NestedWidget {

    private static final ResourceLocation COVER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_cover.png");
    private static final ResourceLocation PAPER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_paper.png");

    public int color = DyeColor.GREEN.getFireworkColor();

    public LinkingBookWidget(String id, int x, int y, float z, int width, int height, Component narration,
            Screen parentScreen, float scale, boolean holdingBook, int color, ILinkData linkData, boolean canLink,
            CompoundTag linkingPanelImage) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.color = color;
        NestedWidget linkingPanel = this.addChild(new LinkingPanelWidget("linking panel", this.x + 155, this.y + 41,
                (int) (z + 1.0F), 64, 42, new TextComponent("Linking Panel"), parentScreen, this.scale, holdingBook,
                linkData, canLink, linkingPanelImage));
        for (GuiEventListener listener : this.listeners) {
            linkingPanel.addListener(listener);
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
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
            this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
        }
    }

}
