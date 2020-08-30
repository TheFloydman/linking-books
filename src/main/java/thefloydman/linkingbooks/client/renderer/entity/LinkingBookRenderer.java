package thefloydman.linkingbooks.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

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

public class LinkingBookRenderer extends EntityRenderer<LinkingBookEntity> {

    private LinkingBookCoverModel coverModel;
    private LinkingBookPagesModel pagesModel;

    private static final ResourceLocation COVER_TEXTURE = Reference
            .getAsResourceLocation("textures/entity/linking_book_cover.png");
    private static final ResourceLocation PAGES_TEXTURE = Reference
            .getAsResourceLocation("textures/entity/linking_book_pages.png");

    public LinkingBookRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        coverModel = new LinkingBookCoverModel();
        pagesModel = new LinkingBookPagesModel();
    }

    @Override
    public void render(LinkingBookEntity entity, float yaw, float partialTicks, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int packedLight) {

        matrixStack.push();

        matrixStack.scale(0.8F, 0.8F, 0.8F);
        matrixStack.rotate(Vector3f.XP.rotation((float) Math.PI));
        matrixStack.rotate(Vector3f.YP.rotation((yaw / 360.0F * (float) Math.PI * 2.0F) - ((float) Math.PI / 2.0F)));
        matrixStack.rotate(Vector3f.ZP.rotation((float) Math.PI / 2 * 3));

        coverModel.setBookState(0.0F, 1.1F);
        pagesModel.setBookState(0.0F, 1.1F);

        float[] color = { 1.0F, 1.0F, 1.0F };
        ItemStack bookStack = entity.getItem();
        if (bookStack != null && !bookStack.isEmpty()) {
            Item item = bookStack.getItem();
            if (item != null && item instanceof WrittenLinkingBookItem) {
                DyeColor dyeColor = ((WrittenLinkingBookItem) item).getColor();
                color = dyeColor.getColorComponentValues();
            }
        }

        if (entity.hurtTime > 0) {
            coverModel.render(matrixStack, buffer.getBuffer(coverModel.getRenderType(COVER_TEXTURE)), 15728880,
                    OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F, 0.4F);
            pagesModel.render(matrixStack, buffer.getBuffer(coverModel.getRenderType(PAGES_TEXTURE)), 15728880,
                    OverlayTexture.NO_OVERLAY, 0.7F, 0.0F, 0.0F, 0.4F);
        } else {
            coverModel.render(matrixStack, buffer.getBuffer(coverModel.getRenderType(COVER_TEXTURE)), 15728880,
                    OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
            pagesModel.render(matrixStack, buffer.getBuffer(coverModel.getRenderType(PAGES_TEXTURE)), 15728880,
                    OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
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