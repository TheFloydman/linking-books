/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.client.renderer.tileentity;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.block.LinkingLecternBlock;
import thefloydman.linkingbooks.blockentity.LinkingLecternBlockEntity;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.client.renderer.entity.model.ModModelLayers;
import thefloydman.linkingbooks.item.LinkingBookItem;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.Reference.Resources;

public class LinkingLecternRenderer implements BlockEntityRenderer<LinkingLecternBlockEntity> {

    private LinkingBookCoverModel coverModel;
    private LinkingBookPagesModel pagesModel;
    private float[] coverColor = { 1.0F, 1.0F, 1.0F };

    public LinkingLecternRenderer(BlockEntityRendererProvider.Context context) {
        this.coverModel = new LinkingBookCoverModel(context.bakeLayer(ModModelLayers.COVER));
        this.coverModel.setBookState(0.95F);
        this.pagesModel = new LinkingBookPagesModel(context.bakeLayer(ModModelLayers.PAGES));
        this.pagesModel.setBookState(0.95F);

    }

    @Override
    public void render(LinkingLecternBlockEntity tileEntity, float arg1, PoseStack matrixStack,
            MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (tileEntity.hasBook()) {

            ItemStack bookStack = tileEntity.getBook();
            if (bookStack != null && !bookStack.isEmpty()) {
                Item item = bookStack.getItem();
                if (item != null && item instanceof WrittenLinkingBookItem) {
                    this.coverColor = new Color(LinkingBookItem.getColor(bookStack, 0))
                            .getRGBColorComponents(this.coverColor);
                }
            }

            matrixStack.pushPose();

            float rotation = 0.0F;
            double[] translate = { 0.0D, 0.0D, 0.0D };
            switch (tileEntity.getBlockState().getValue(LinkingLecternBlock.FACING)) {
                case NORTH:
                    rotation = 1.0F;
                    translate[0] = 0.5D;
                    translate[1] = 1.0D;
                    translate[2] = 0.4D;
                    break;
                case WEST:
                    rotation = 2.0F;
                    translate[0] = 0.4D;
                    translate[1] = 1.0D;
                    translate[2] = 0.5D;
                    break;
                case SOUTH:
                    rotation = 3.0F;
                    translate[0] = 0.5D;
                    translate[1] = 1.0D;
                    translate[2] = 0.6D;
                    break;
                case EAST:
                    rotation = 0.0F;
                    translate[0] = 0.6D;
                    translate[1] = 1.0D;
                    translate[2] = 0.5D;
                    break;
                default:
                    rotation = 1.0F;
                    translate[0] = 0.5D;
                    translate[1] = 1.0D;
                    translate[2] = 0.4D;
            }
            matrixStack.translate(translate[0], translate[1], translate[2]);
            matrixStack.mulPose(Axis.YP.rotation((float) Math.PI * rotation / 2.0F));
            matrixStack.mulPose(Axis.XP.rotation((float) Math.PI));
            matrixStack.mulPose(Axis.ZP.rotation((float) -Math.PI / 2.67F));
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            VertexConsumer vertexBuilder = buffer.getBuffer(this.coverModel.renderType(Resources.LINKING_BOOK_TEXTURE));
            this.coverModel.renderToBuffer(matrixStack, vertexBuilder, combinedLight, combinedOverlay,
                    this.coverColor[0], this.coverColor[1], this.coverColor[2], 1.0F);
            this.pagesModel.renderToBuffer(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F,
                    1.0F);

            matrixStack.popPose();

        }
    }

}
