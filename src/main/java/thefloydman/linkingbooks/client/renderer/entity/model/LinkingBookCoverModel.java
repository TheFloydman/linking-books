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
package thefloydman.linkingbooks.client.renderer.entity.model;

import java.util.Arrays;
import java.util.List;

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
import thefloydman.linkingbooks.entity.LinkingBookEntity;

public class LinkingBookCoverModel extends EntityModel<LinkingBookEntity> {

    private final ModelPart coverRight;
    private final ModelPart coverLeft;
    private final ModelPart bookSpine;
    private final List<ModelPart> allModels;

    public LinkingBookCoverModel(ModelPart model) {
        super(RenderType::entitySolid);
        this.coverRight = model.getChild("right_cover");
        this.coverLeft = model.getChild("left_cover");
        this.bookSpine = model.getChild("spine");
        this.allModels = Arrays.asList(this.coverRight, this.coverLeft, this.bookSpine);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
            float red, float green, float blue, float alpha) {
        this.allModels.forEach((model) -> {
            model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("right_cover",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F),
                PartPose.offset(0.0F, 0.0F, -1.0F));
        partdefinition.addOrReplaceChild("left_cover",
                CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F),
                PartPose.offset(0.0F, 0.0F, 1.0F));
        partdefinition.addOrReplaceChild("spine",
                CubeListBuilder.create().texOffs(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F),
                PartPose.rotation(0.0F, (float) Math.PI / 2.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void setBookState(float openAmount) {
        float radians = Mth.clamp((float) Math.PI / 2.0F * openAmount, 0.0F, (float) Math.PI / 2.0F);
        this.coverRight.yRot = (float) Math.PI + radians;
        this.coverLeft.yRot = -radians;
    }

    @Override
    public void setupAnim(LinkingBookEntity arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
    }

}