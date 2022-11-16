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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.client.renderer.entity.model.ModModelLayers;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.Resources;

public class LinkingBookRenderer extends EntityRenderer<LinkingBookEntity> {

    private LinkingBookCoverModel coverModel;
    private LinkingBookPagesModel pagesModel;
    private float[] color = { 0.0F, 1.0F, 0.0F };

    public LinkingBookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.coverModel = new LinkingBookCoverModel(context.bakeLayer(ModModelLayers.COVER));
        this.coverModel.setBookState(0.9F);
        this.pagesModel = new LinkingBookPagesModel(context.bakeLayer(ModModelLayers.PAGES));
        this.pagesModel.setBookState(0.9F);
    }

    @Override
    public void render(LinkingBookEntity entity, float yaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {

        ItemStack bookStack = entity.getItem();
        if (bookStack != null && !bookStack.isEmpty()) {
            Item item = bookStack.getItem();
            if (item != null && item instanceof WrittenLinkingBookItem) {
                if (color != null) {
                    this.color = new Color(LinkingBookItem.getColor(bookStack, 0)).getRGBColorComponents(this.color);
                }
            }
        }

        matrixStack.pushPose();

        matrixStack.scale(0.75F, 0.75F, 0.75F);
        matrixStack.mulPose(Vector3f.XP.rotation((float) Math.PI));
        matrixStack.mulPose(Vector3f.YP.rotation((yaw / 360.0F * (float) Math.PI * 2.0F) - ((float) Math.PI / 2.0F)));
        matrixStack.mulPose(Vector3f.ZP.rotation((float) Math.PI / 2 * 3));

        VertexConsumer vertexBuilder = buffer.getBuffer(this.coverModel.renderType(Resources.LINKING_BOOK_TEXTURE));

        if (entity.hurtTime > 0) {
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 0.7F,
                    0.0F, 0.0F, 0.4F);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 0.7F,
                    0.0F, 0.0F, 0.4F);
        } else {
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY,
                    this.color[0], this.color[1], this.color[2], 1.0F);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F,
                    1.0F, 1.0F, 1.0F);
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