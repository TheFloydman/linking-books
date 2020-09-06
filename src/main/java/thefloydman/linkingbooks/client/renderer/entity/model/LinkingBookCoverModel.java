package thefloydman.linkingbooks.client.renderer.entity.model;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import thefloydman.linkingbooks.entity.LinkingBookEntity;

public class LinkingBookCoverModel extends EntityModel<LinkingBookEntity> {

    private final ModelRenderer coverRight = new ModelRenderer(64, 32, 0, 0).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F,
            0.005F);
    private final ModelRenderer coverLeft = new ModelRenderer(64, 32, 16, 0).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F,
            0.005F);
    private final ModelRenderer bookSpine = new ModelRenderer(64, 32, 12, 0).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F,
            0.005F);
    private final List<ModelRenderer> allModels = Arrays.asList(this.coverRight, this.coverLeft, this.bookSpine);

    public LinkingBookCoverModel() {
        super(RenderType::getEntitySolid);
        this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.bookSpine.rotateAngleY = ((float) Math.PI / 2F);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
            float red, float green, float blue, float alpha) {
        this.allModels.forEach((model) -> {
            model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public void setBookState(float openAmount) {
        float radians = MathHelper.clamp((float) Math.PI / 2.0F * openAmount, 0.0F, (float) Math.PI / 2.0F);
        this.coverRight.rotateAngleY = (float) Math.PI + radians;
        this.coverLeft.rotateAngleY = -radians;
    }

    @Override
    public void setRotationAngles(LinkingBookEntity arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
    }

}