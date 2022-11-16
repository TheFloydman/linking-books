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
package thefloydman.linkingbooks.client.renderer.entity;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.Resources;

public class LinkingBookRenderer extends EntityRenderer<LinkingBookEntity> {

    private LinkingBookCoverModel coverModel = new LinkingBookCoverModel();
    private LinkingBookPagesModel pagesModel = new LinkingBookPagesModel();
    private float[] color = { 0.0F, 1.0F, 0.0F };

    public LinkingBookRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.coverModel.setBookState(0.9F);
        this.pagesModel.setBookState(0.9F);
    }

    @Override
    public void render(LinkingBookEntity entity, float yaw, float partialTicks, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int packedLight) {

        ItemStack bookStack = entity.getItem();
        if (bookStack != null && !bookStack.isEmpty()) {
            Item item = bookStack.getItem();
            if (item != null && item instanceof WrittenLinkingBookItem) {
                IColorCapability color = bookStack.getCapability(ColorCapability.COLOR).orElse(null);
                if (color != null) {
                    this.color = new Color(color.getColor()).getRGBColorComponents(this.color);
                }
            }
        }

        matrixStack.pushPose();

        matrixStack.scale(0.75F, 0.75F, 0.75F);
        matrixStack.mulPose(Vector3f.XP.rotation((float) Math.PI));
        matrixStack.mulPose(Vector3f.YP.rotation((yaw / 360.0F * (float) Math.PI * 2.0F) - ((float) Math.PI / 2.0F)));
        matrixStack.mulPose(Vector3f.ZP.rotation((float) Math.PI / 2 * 3));

        IVertexBuilder vertexBuilder = buffer.getBuffer(this.coverModel.renderType(Resources.LINKING_BOOK_TEXTURE));

        if (entity.hurtTime > 0) {
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F,
                    0.4F);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F,
                    0.4F);
        } else {
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, this.color[0],
                    this.color[1], this.color[2], 1.0F);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                    1.0F);
        }

        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LinkingBookEntity entity) {
        return Reference.getAsResourceLocation("textures/entity/linking_book.png");
    }

    @Override
    protected boolean shouldShowName(LinkingBookEntity entity) {
        return super.shouldShowName(entity) && (entity.shouldShowName()
                || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

}