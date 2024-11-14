package thefloydman.linkingbooks.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;

import java.util.Arrays;
import java.util.List;

public class LinkingBookPagesModel extends EntityModel<LinkingBookEntity> {

    private final ModelPart pagesRight;
    private final ModelPart pagesLeft;
    private final List<ModelPart> allModels;

    public LinkingBookPagesModel(ModelPart model) {
        super(RenderType::entitySolid);
        this.pagesRight = model.getChild("right_pages");
        this.pagesLeft = model.getChild("left_pages");
        this.allModels = Arrays.asList(this.pagesRight, this.pagesLeft);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("right_pages",
                CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_pages",
                CubeListBuilder.create().texOffs(12, 10).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color) {
        this.allModels.forEach((model) -> {
            model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, color);
        });
    }

    public void setBookState(float openAmount) {
        float radians = Mth.clamp((float) Math.PI / 2.0F * openAmount, 0.0F, (float) Math.PI / 2.0F);
        this.pagesRight.yRot = radians;
        this.pagesLeft.yRot = -radians;
        this.pagesRight.x = Mth.sin(radians);
        this.pagesLeft.x = Mth.sin(radians);
    }

    @Override
    public void setupAnim(LinkingBookEntity arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
    }

}