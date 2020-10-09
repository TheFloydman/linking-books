package thefloydman.linkingbooks.client.renderer.tileentity;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.block.LinkingLecternBlock;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.tileentity.LinkingLecternTileEntity;
import thefloydman.linkingbooks.util.Reference.Resources;

public class LinkingLecternRenderer extends TileEntityRenderer<LinkingLecternTileEntity> {

    private LinkingBookCoverModel coverModel = new LinkingBookCoverModel();
    private LinkingBookPagesModel pagesModel = new LinkingBookPagesModel();
    private float[] color = { 1.0F, 1.0F, 1.0F };

    public LinkingLecternRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.coverModel.setBookState(0.95F);
        this.pagesModel.setBookState(0.95F);
    }

    @Override
    public void render(LinkingLecternTileEntity tileEntity, float arg1, MatrixStack matrixStack,
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
            switch (tileEntity.getBlockState().get(LinkingLecternBlock.FACING)) {
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
            matrixStack.rotate(Vector3f.YP.rotation((float) Math.PI * rotation / 2.0F));
            matrixStack.rotate(Vector3f.XP.rotation((float) Math.PI));
            matrixStack.rotate(Vector3f.ZP.rotation((float) -Math.PI / 2.67F));
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            IVertexBuilder vertexBuilder = buffer
                    .getBuffer(this.coverModel.getRenderType(Resources.LINKING_BOOK_TEXTURE));
            this.coverModel.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, this.color[0],
                    this.color[1], this.color[2], 1.0F);
            this.pagesModel.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                    1.0F);

            matrixStack.pop();

        }
    }

}
