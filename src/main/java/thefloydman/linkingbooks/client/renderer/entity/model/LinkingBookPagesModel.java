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

public class LinkingBookPagesModel extends EntityModel<LinkingBookEntity> {

    private final ModelRenderer pagesRight = new ModelRenderer(64, 32, 0, 10).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F,
            1.0F);
    private final ModelRenderer pagesLeft = new ModelRenderer(64, 32, 12, 10).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F,
            1.0F);
    private final List<ModelRenderer> allModels = Arrays.asList(this.pagesRight, this.pagesLeft);

    public LinkingBookPagesModel() {
        super(RenderType::getEntitySolid);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
            float red, float green, float blue, float alpha) {
        this.allModels.forEach((model) -> {
            model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public void setBookState(float openAmount) {
        float radians = MathHelper.clamp((float) Math.PI / 2.0F * openAmount, 0.0F, (float) Math.PI / 2.0F);
        this.pagesRight.rotateAngleY = radians;
        this.pagesLeft.rotateAngleY = -radians;
        this.pagesRight.rotationPointX = MathHelper.sin(radians);
        this.pagesLeft.rotationPointX = MathHelper.sin(radians);
    }

    @Override
    public void setRotationAngles(LinkingBookEntity arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
    }

}