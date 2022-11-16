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
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

    public int color = DyeColor.GREEN.getColorValue();

    public LinkingBookWidget(int x, int y, float zLevel, int width, int height, ITextComponent narration,
            boolean holdingBook, int color, ILinkData linkData, boolean canLink, CompoundNBT linkingPanelImage) {
        super(x, y, width, height, narration);
        this.color = color;
        NestedWidget linkingPanel = this.addChild(new LinkingPanelWidget(this.x + 155, this.y + 41, 0.0F, 64, 42,
                new StringTextComponent("Linking Panel"), holdingBook, linkData, canLink, linkingPanelImage));
        for (IGuiEventListener listener : this.listeners) {
            linkingPanel.addListener(listener);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        matrixStack.pushPose();
        RenderSystem.pushMatrix();

        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                DestFactor.ZERO);
        Minecraft.getInstance().getTextureManager().bind(COVER_TEXTURE);
        float[] color = new Color(this.color).getRGBColorComponents(null);
        RenderSystem.color4f(MathHelper.clamp(color[0], 0.1F, 1.0F), MathHelper.clamp(color[1], 0.1F, 1.0F),
                MathHelper.clamp(color[2], 0.1F, 1.0F), 1.0F);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.width, this.height);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bind(PAPER_TEXTURE);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.width, this.height);

        RenderSystem.popMatrix();
        matrixStack.popPose();

        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

}
