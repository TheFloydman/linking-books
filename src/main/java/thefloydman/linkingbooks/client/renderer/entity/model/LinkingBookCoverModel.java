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
        super(RenderType::entitySolid);
        this.coverRight.setPos(0.0F, 0.0F, -1.0F);
        this.coverLeft.setPos(0.0F, 0.0F, 1.0F);
        this.bookSpine.yRot = ((float) Math.PI / 2F);
    }

    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
            float red, float green, float blue, float alpha) {
        this.allModels.forEach((model) -> {
            model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public void setBookState(float openAmount) {
        float radians = MathHelper.clamp((float) Math.PI / 2.0F * openAmount, 0.0F, (float) Math.PI / 2.0F);
        this.coverRight.yRot = (float) Math.PI + radians;
        this.coverLeft.yRot = -radians;
    }

    @Override
    public void setupAnim(LinkingBookEntity arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
    }

}