/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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