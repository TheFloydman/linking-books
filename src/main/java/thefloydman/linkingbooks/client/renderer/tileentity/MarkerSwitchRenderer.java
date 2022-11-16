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
package thefloydman.linkingbooks.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import thefloydman.linkingbooks.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.blockentity.MarkerSwitchBlockEntity;

public class MarkerSwitchRenderer implements BlockEntityRenderer<MarkerSwitchBlockEntity> {

    private final ItemRenderer itemRenderer;

    public MarkerSwitchRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(MarkerSwitchBlockEntity tileEntity, float arg1, PoseStack matrixStack, MultiBufferSource buffer,
            int arg4, int arg5) {
        BlockState blockState = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos());
        if (blockState.getBlock() instanceof MarkerSwitchBlock) {
            if (blockState.getValue(MarkerSwitchBlock.OPEN) == true && tileEntity.getLevel()
                    .getBlockState(tileEntity.getBlockPos()).getValue(MarkerSwitchBlock.HALF) == DoubleBlockHalf.LOWER
                    && tileEntity.hasItem()) {
                ItemStack itemStack = tileEntity.getItem();
                matrixStack.pushPose();
                matrixStack.translate(0.5D, 0.6D, 0.5D);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                Direction facing = tileEntity.getBlockState().getValue(MarkerSwitchBlock.FACING);
                float rotation = facing == Direction.EAST || facing == Direction.WEST ? facing.toYRot() + 45.0F
                        : facing.toYRot() - 135.0F;
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                int lightAbove = LevelRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());
                this.itemRenderer.renderStatic(itemStack, TransformType.FIXED, lightAbove, OverlayTexture.NO_OVERLAY,
                        matrixStack, buffer, lightAbove);
                matrixStack.popPose();
            }
        }
    }

}
