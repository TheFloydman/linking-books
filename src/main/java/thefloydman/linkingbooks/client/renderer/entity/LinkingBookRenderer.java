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

package thefloydman.linkingbooks.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;
import thefloydman.linkingbooks.world.item.WrittenLinkingBookItem;

import java.awt.*;

public class LinkingBookRenderer extends EntityRenderer<LinkingBookEntity> {

    private LinkingBookCoverModel coverModel;
    private LinkingBookPagesModel pagesModel;
    private int color = new Color(0.0F, 1.0F, 0.0F, 1.0F).getRGB();

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
            if (item instanceof WrittenLinkingBookItem) {
                this.color = LinkingUtils.getLinkingBookColor(bookStack, 0);
            }
        }

        matrixStack.pushPose();

        matrixStack.scale(0.75F, 0.75F, 0.75F);
        matrixStack.mulPose(Axis.XP.rotation((float) Math.PI));
        matrixStack.mulPose(Axis.YP.rotation((yaw / 360.0F * (float) Math.PI * 2.0F) - ((float) Math.PI / 2.0F)));
        matrixStack.mulPose(Axis.ZP.rotation((float) Math.PI / 2 * 3));

        VertexConsumer vertexBuilder = buffer.getBuffer(this.coverModel.renderType(Reference.Resources.LINKING_BOOK_TEXTURE));

        if (entity.hurtTime > 0) {
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, new Color(0.7F, 0.0F, 0.0F, 0.4F).getRGB());
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, new Color(0.7F, 0.0F, 0.0F, 0.4F).getRGB());
        } else {
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, this.color);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
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