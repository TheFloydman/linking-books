package thefloydman.linkingbooks.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import thefloydman.linkingbooks.client.renderer.entity.model.LinkingBookCoverModel;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;

import javax.annotation.Nonnull;

public class LinkingBookCoverLayer extends RenderLayer<LinkingBookEntity, LinkingBookCoverModel> {

    public LinkingBookCoverLayer(RenderLayerParent<LinkingBookEntity, LinkingBookCoverModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLightIn, @Nonnull LinkingBookEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                       float headPitch) {
    }

}