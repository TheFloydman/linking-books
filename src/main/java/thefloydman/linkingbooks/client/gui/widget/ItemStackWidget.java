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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ItemStackWidget extends NestedWidget {

    protected List<ItemStack> itemStacks;
    public long creationTime;
    public long changeTime = 2000L; // 2 seconds

    public ItemStackWidget(String id, int x, int y, float z, int width, int height, Component narration,
            Screen parentScreen, float scale, List<ItemStack> itemStack) {
        super(id, x, y, z, width, height, narration, parentScreen, scale);
        this.itemStacks = itemStack;
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            int changeIndex = Mth.fastFloor((System.currentTimeMillis() - this.creationTime) / this.changeTime);
            ItemStack stack = this.itemStacks.get(changeIndex % this.itemStacks.size());
            if (stack.isEmpty())
                return;
            poseStack.pushPose();
            ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
            BakedModel bakedModel = itemRenderer.getModel(stack, null, null, 0);
            BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();
            this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            PoseStack pStack = RenderSystem.getModelViewStack();
            pStack.pushPose();
            pStack.translate(this.x + this.scale * 9.0D, this.y + this.scale * 9.0D, itemRenderer.blitOffset);
            pStack.scale(16.0F, -16.0F, 16.0F);
            RenderSystem.applyModelViewMatrix();
            if (!bakedModel.usesBlockLight())
                GlStateManager.setupGuiFlatDiffuseLighting(new Vector3f(1.0F, 1.0F, -1.0F),
                        new Vector3f(1.0F, 1.0F, -1.0F));
            itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, poseStack, bufferSource, 15728880,
                    OverlayTexture.NO_OVERLAY, bakedModel);
            bufferSource.endBatch();
            RenderSystem.enableDepthTest();
            if (!bakedModel.usesBlockLight())
                Lighting.setupFor3DItems();
            pStack.popPose();
            RenderSystem.applyModelViewMatrix();
            if (stack.getCount() > 1) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, 10.0D);
                int xOff = 9;
                int yOff = 6;
                this.minecraft.font.draw(poseStack, String.valueOf(stack.getCount()), (this.x + xOff) / this.scale,
                        (this.y + yOff) / this.scale, Color.BLACK.getRGB());
                poseStack.popPose();
            }
            if (this.isInside(mouseX, mouseY)) {
                this.parentScreen.renderTooltip(new PoseStack(), stack.getItem().getName(stack), mouseX, mouseY);
            }
            poseStack.popPose();
        }
    }

    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY, ItemStack stack) {
    }

    @Override
    public void restore(NestedWidget backup) {
        if (backup instanceof ItemStackWidget) {
            ItemStackWidget old = (ItemStackWidget) backup;
            this.creationTime = old.creationTime;
        }
    }

}
