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
package thefloydman.linkingbooks.client.renderer.tileentity;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.block.LinkTranslatorBlock;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.tileentity.LinkTranslatorTileEntity;
import thefloydman.linkingbooks.util.Reference.Resources;

public class LinkTranslatorRenderer extends TileEntityRenderer<LinkTranslatorTileEntity> {

    private LinkingBookCoverModel coverModel = new LinkingBookCoverModel();
    private LinkingBookPagesModel pagesModel = new LinkingBookPagesModel();
    private float[] color = { 1.0F, 1.0F, 1.0F };

    public LinkTranslatorRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.coverModel.setBookState(0.95F);
        this.pagesModel.setBookState(0.95F);
    }

    @Override
    public void render(LinkTranslatorTileEntity tileEntity, float arg1, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int arg4, int arg5) {
        if (tileEntity.hasBook()) {

            ItemStack bookStack = tileEntity.getBook();
            if (bookStack != null && !bookStack.isEmpty()) {
                Item item = bookStack.getItem();
                if (item != null && item instanceof WrittenLinkingBookItem) {
                    IColorCapability color = bookStack.getCapability(ColorCapability.COLOR).orElse(null);
                    if (color != null) {
                        this.color = new Color(color.getColor()).getRGBColorComponents(this.color);
                    }
                }
            }

            matrixStack.push();

            float rotation = 0.0F;
            double[] translate = { 0.0D, 0.0D, 0.0D };
            switch (tileEntity.getBlockState().get(LinkTranslatorBlock.HORIZONTAL_FACING)) {
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
            matrixStack.rotate(Vector3f.YP.rotation((float) Math.PI * rotation / 2.0F));
            matrixStack.rotate(Vector3f.XP.rotation((float) Math.PI));
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            IVertexBuilder vertexBuilder = buffer
                    .getBuffer(this.coverModel.getRenderType(Resources.LINKING_BOOK_TEXTURE));
            this.coverModel.render(matrixStack, vertexBuilder, 32767, arg5, this.color[0], this.color[1], this.color[2],
                    1.0F);
            this.pagesModel.render(matrixStack, vertexBuilder, 32767, arg5, 1.0F, 1.0F, 1.0F, 1.0F);

            matrixStack.pop();

        }
    }

}
