package thefloydman.linkingbooks.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.entity.LinkingBookEntity;

public class LinkingBookCoverLayer extends LayerRenderer<LinkingBookEntity, LinkingBookCoverModel> {

    public LinkingBookCoverLayer(IEntityRenderer<LinkingBookEntity, LinkingBookCoverModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, LinkingBookEntity entity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch) {

    }

}
