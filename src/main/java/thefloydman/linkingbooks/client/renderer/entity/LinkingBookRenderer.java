package thefloydman.linkingbooks.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookPagesModel;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.Resources;

public class LinkingBookRenderer extends EntityRenderer<LinkingBookEntity> {

    private LinkingBookCoverModel coverModel = new LinkingBookCoverModel();
    private LinkingBookPagesModel pagesModel = new LinkingBookPagesModel();

    public LinkingBookRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(LinkingBookEntity entity, float yaw, float partialTicks, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int packedLight) {

        matrixStack.push();

        matrixStack.scale(0.5F, 0.5F, 0.5F);
        matrixStack.rotate(Vector3f.XP.rotation((float) Math.PI));
        matrixStack.rotate(Vector3f.YP.rotation((yaw / 360.0F * (float) Math.PI * 2.0F) - ((float) Math.PI / 2.0F)));
        matrixStack.rotate(Vector3f.ZP.rotation((float) Math.PI / 2 * 3));

        this.coverModel.setBookState(0.9F);
        this.pagesModel.setBookState(0.9F);

        float[] color = { 1.0F, 1.0F, 1.0F };
        ItemStack bookStack = entity.getItem();
        if (bookStack != null && !bookStack.isEmpty()) {
            Item item = bookStack.getItem();
            if (item != null && item instanceof WrittenLinkingBookItem) {
                DyeColor dyeColor = ((WrittenLinkingBookItem) item).getColor();
                color = dyeColor.getColorComponentValues();
            }
        }

        IVertexBuilder vertexBuilder = buffer.getBuffer(this.coverModel.getRenderType(Resources.LINKING_BOOK_TEXTURE));

        if (entity.hurtTime > 0) {
            this.coverModel.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F,
                    0.4F);
            this.pagesModel.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F,
                    0.4F);
        } else {
            this.coverModel.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, color[0], color[1],
                    color[2], 1.0F);
            this.pagesModel.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                    1.0F);
        }

        matrixStack.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(LinkingBookEntity entity) {
        return Reference.getAsResourceLocation("textures/entity/linking_book.png");
    }

    @Override
    protected boolean canRenderName(LinkingBookEntity entity) {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender()
                || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }

}