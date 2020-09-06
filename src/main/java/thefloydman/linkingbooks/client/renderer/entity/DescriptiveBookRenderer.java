package thefloydman.linkingbooks.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.entity.DescriptiveBookEntity;
import thefloydman.linkingbooks.util.Reference;

public class DescriptiveBookRenderer extends EntityRenderer<DescriptiveBookEntity> {

    private LinkingBookCoverModel bookModel;

    public DescriptiveBookRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        bookModel = new LinkingBookCoverModel();
    }

    @Override
    public void render(DescriptiveBookEntity entity, float yaw, float partialTicks, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.push();
        matrixStack.scale(0.8F, 0.8F, 0.8F);
        matrixStack.rotate(Vector3f.XP.rotation((float) Math.PI));
        matrixStack.rotate(Vector3f.YP.rotation(yaw));
        matrixStack.rotate(Vector3f.ZP.rotation((float) Math.PI / 2 * 3));
        bookModel.setBookState(1.1F);
        if (entity.hurtTime > 0) {
            bookModel.render(matrixStack, buffer.getBuffer(bookModel.getRenderType(this.getEntityTexture(entity))),
                    15728880, OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F, 0.4F);
        } else {
            bookModel.render(matrixStack, buffer.getBuffer(bookModel.getRenderType(this.getEntityTexture(entity))),
                    15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrixStack.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(DescriptiveBookEntity entity) {
        return new ResourceLocation(Reference.MOD_ID, "textures/entity/linking_book.png");
    }

    @Override
    protected boolean canRenderName(DescriptiveBookEntity entity) {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender()
                || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }

}