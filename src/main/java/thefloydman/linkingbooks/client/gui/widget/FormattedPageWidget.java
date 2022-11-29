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

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookImage;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookParagraph;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookRecipe;
import thefloydman.linkingbooks.util.Reference;

@OnlyIn(Dist.CLIENT)
public class FormattedPageWidget extends NestedWidget {

    private static final ResourceLocation CRAFTING_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/guidebook/crafting.png");

    private Font font;
    private List<Object> preparedElements;
    private List<Object> rawElements;
    private float scale = 0.5F;
    public long creationTime;
    public long changeTime = 2000L;

    public FormattedPageWidget(String id, int x, int y, int width, int height, Component narration, Font font,
            List<Object> rawElements) {
        super(id, x, y, width, height, narration);
        this.font = font;
        this.creationTime = System.currentTimeMillis();
        this.rawElements = rawElements;
        if (this.preparedElements == null) {
            this.preparedElements = this.prepareElements(this.rawElements, Float.valueOf(this.width) / this.scale);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible)
            return;
        int lineSpacing = 6;
        int currentY = 0;
        for (Object object : preparedElements) {
            if (object instanceof GuidebookParagraph) {
                GuidebookParagraph paragraph = GuidebookParagraph.class.cast(object);
                poseStack.pushPose();
                poseStack.scale(this.scale, this.scale, 1.0F);
                List<Component> paragraphLines = paragraph.renderable;
                for (int k = 0; k < paragraphLines.size(); k++, currentY += lineSpacing) {
                    this.font.draw(poseStack, paragraphLines.get(k), Float.valueOf(this.x) / this.scale,
                            (Float.valueOf(this.y) + currentY) / this.scale, (int) this.zLevel + 1);
                }
                currentY += lineSpacing;
                poseStack.popPose();
            } else if (object instanceof GuidebookImage) {
                GuidebookImage textureInfo = GuidebookImage.class.cast(object);
                poseStack.pushPose();
                poseStack.scale(this.scale, this.scale, 1.0F);
                RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                        DestFactor.ZERO);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, textureInfo.resourceLocation);
                float localImageScale = (this.width / this.scale) / textureInfo.sourceWidth;
                int localImageWidth = (int) (textureInfo.sourceWidth * localImageScale * textureInfo.scale);
                int localImageHeight = (int) (textureInfo.sourceHeight * localImageScale * textureInfo.scale);
                int localImageX = (int) (((this.width / this.scale) - localImageWidth) / 2.0F);
                blit(poseStack, (int) (Float.valueOf(this.x) / this.scale) + localImageX,
                        (int) ((Float.valueOf(this.y) + currentY) / this.scale), 1, 0, 0, localImageWidth,
                        localImageHeight, localImageWidth, localImageHeight);
                poseStack.popPose();
                currentY += (localImageHeight * this.scale) + lineSpacing;
            } else if (object instanceof GuidebookRecipe) {
                GuidebookRecipe recipeInfo = GuidebookRecipe.class.cast(object);
                int changeIndex = Mth.fastFloor((System.currentTimeMillis() - this.creationTime) / this.changeTime);
                poseStack.pushPose();
                poseStack.scale(this.scale, this.scale, 1.0F);
                List<List<ItemStack>> recipe = recipeInfo.renderable;
                RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                        DestFactor.ZERO);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, CRAFTING_TEXTURE);
                int gridWidth = 105;
                int gridHeight = 50;
                int gridTextureDimension = 256;
                int centeredX = (int) ((Float.valueOf(this.x) / this.scale)
                        + (((this.width / this.scale) - gridWidth) / 2));
                blit(poseStack, centeredX, (int) ((Float.valueOf(this.y) + currentY) / this.scale), 1, 0, 0, gridWidth,
                        gridHeight, gridTextureDimension, gridTextureDimension);
                for (int j = 0; j < recipe.size(); j++) {
                    List<ItemStack> list = recipe.get(j);
                    for (int k = 0; k < list.size(); k++) {
                        ItemStack itemStack = list.get(changeIndex % list.size());
                        ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
                        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
                        BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();
                        this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false,
                                false);
                        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                        RenderSystem.enableBlend();
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        PoseStack pStack = RenderSystem.getModelViewStack();
                        pStack.pushPose();
                        double localIngredientX = (centeredX * this.scale)
                                + ((j == 9 ? 88 : (j % 3) * 17.0D) * this.scale);
                        double localIngredientY = this.y + currentY
                                + ((j == 9 ? 17 : Mth.floor(j / 3) * 17.0D) * this.scale);
                        pStack.translate(localIngredientX + this.scale * 8.0D, localIngredientY + this.scale * 8.0D,
                                100.0F + itemRenderer.blitOffset);
                        pStack.scale(16.0F, -16.0F, 16.0F);
                        RenderSystem.applyModelViewMatrix();
                        if (!bakedModel.usesBlockLight())
                            GlStateManager.setupGuiFlatDiffuseLighting(new Vector3f(1.0F, 1.0F, -1.0F),
                                    new Vector3f(1.0F, 1.0F, -1.0F));
                        this.minecraft.getItemRenderer().render(itemStack, ItemTransforms.TransformType.GUI, false,
                                poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
                        bufferSource.endBatch();
                        RenderSystem.enableDepthTest();
                        if (!bakedModel.usesBlockLight())
                            Lighting.setupFor3DItems();
                        pStack.popPose();
                        RenderSystem.applyModelViewMatrix();
                        if (itemStack.getCount() > 1)
                            this.font.draw(poseStack, String.valueOf(itemStack.getCount()), (this.x / this.scale) + 148,
                                    ((this.y + currentY) / this.scale) + 32, 100);
                    }
                }
                poseStack.popPose();
                currentY += (gridHeight * this.scale) + lineSpacing;
            }
        }
    }

    public List<Object> prepareElements(List<Object> source, float width) {
        List<Object> output = Lists.newArrayList();
        for (Object object : source) {
            if (object instanceof GuidebookParagraph) {
                GuidebookParagraph paragraph = GuidebookParagraph.class.cast(object);
                paragraph.makeRenderable(this.font, width);
                output.add(paragraph);
            } else if (object instanceof GuidebookImage) {
                output.add(object);
            } else if (object instanceof GuidebookRecipe) {
                GuidebookRecipe recipe = GuidebookRecipe.class.cast(object);
                recipe.makeRenderable();
                output.add(recipe);
            }
        }
        return output;
    }

    @Override
    public void restore(NestedWidget backup) {
        FormattedPageWidget old = FormattedPageWidget.class.cast(backup);
        if (old != null) {
            this.creationTime = old.creationTime;
        }
    }

}
