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

package thefloydman.linkingbooks.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.client.renderer.entity.model.ModModelLayers;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.level.block.LinkTranslatorBlock;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;

import javax.annotation.Nonnull;
import java.awt.*;

public class LinkTranslatorRenderer implements BlockEntityRenderer<LinkTranslatorBlockEntity> {

    private final LinkingBookCoverModel coverModel;
    private final LinkingBookPagesModel pagesModel;

    public LinkTranslatorRenderer(BlockEntityRendererProvider.Context context) {
        this.coverModel = new LinkingBookCoverModel(context.bakeLayer(ModModelLayers.COVER));
        this.coverModel.setBookState(0.95F);
        this.pagesModel = new LinkingBookPagesModel(context.bakeLayer(ModModelLayers.PAGES));
        this.pagesModel.setBookState(0.95F);
    }

    @Override
    public void render(LinkTranslatorBlockEntity tileEntity, float arg1, @Nonnull PoseStack matrixStack,
                       @Nonnull MultiBufferSource buffer, int arg4, int arg5) {
        if (tileEntity.hasBook()) {

            ItemStack bookStack = tileEntity.getBook();
            int coverColor = LinkingUtils.getLinkingBookColor(bookStack, 0);

            matrixStack.pushPose();

            float rotation = 0.0F;
            double[] translate = {0.0D, 0.0D, 0.0D};
            switch (tileEntity.getBlockState().getValue(LinkTranslatorBlock.FACING)) {
                case NORTH:
                    rotation = 1.0F;
                    translate[0] = 0.5D;
                    translate[1] = 0.5D;
                    translate[2] = 1.0D / 8.0D - 0.01D;
                    break;
                case WEST:
                    rotation = 2.0F;
                    translate[0] = 1.0D / 8.0D - 0.01D;
                    translate[1] = 0.5D;
                    translate[2] = 0.5D;
                    break;
                case SOUTH:
                    rotation = 3.0F;
                    translate[0] = 0.5D;
                    translate[1] = 0.5D;
                    translate[2] = 1.0D - 1.0D / 8.0D + 0.01D;
                    break;
                case EAST:
                    rotation = 0.0F;
                    translate[0] = 1.0D - 1.0D / 8.0D + 0.01D;
                    translate[1] = 0.5D;
                    translate[2] = 0.5D;
                    break;
                default:
                    rotation = 1.0F;
                    translate[0] = 0.5D;
                    translate[1] = 0.5D;
                    translate[2] = 0.4D;
            }
            matrixStack.translate(translate[0], translate[1], translate[2]);
            matrixStack.mulPose(Axis.YP.rotation((float) Math.PI * rotation / 2.0F));
            matrixStack.mulPose(Axis.XP.rotation((float) Math.PI));
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            VertexConsumer vertexBuilder = buffer.getBuffer(this.coverModel.renderType(Reference.Resources.LINKING_BOOK_TEXTURE));
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, arg4, arg5, coverColor);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, arg4, arg5, Color.WHITE.getRGB());

            matrixStack.popPose();

        }
    }

}