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

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import thefloydman.linkingbooks.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.tileentity.MarkerSwitchTileEntity;

public class MarkerSwitchRenderer extends TileEntityRenderer<MarkerSwitchTileEntity> {

    private final ItemRenderer itemRenderer;

    public MarkerSwitchRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(MarkerSwitchTileEntity tileEntity, float arg1, MatrixStack matrixStack, IRenderTypeBuffer buffer,
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
                float rotation = facing == Direction.EAST || facing == Direction.WEST
                        ? facing.toYRot() + 45.0F
                        : facing.toYRot() - 135.0F;
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                int lightAbove = WorldRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());
                this.itemRenderer.renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, lightAbove,
                        OverlayTexture.NO_OVERLAY, matrixStack, buffer);
                matrixStack.popPose();
            }
        }
    }

}
