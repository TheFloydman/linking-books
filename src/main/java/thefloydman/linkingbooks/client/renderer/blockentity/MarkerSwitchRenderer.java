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

package thefloydman.linkingbooks.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.joml.Quaternionf;
import thefloydman.linkingbooks.world.level.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.world.level.block.entity.MarkerSwitchBlockEntity;

import javax.annotation.Nonnull;

public class MarkerSwitchRenderer implements BlockEntityRenderer<MarkerSwitchBlockEntity> {

    private final ItemRenderer itemRenderer;

    public MarkerSwitchRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(MarkerSwitchBlockEntity tileEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState blockState = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos());
        if (blockState.getBlock() instanceof MarkerSwitchBlock) {
            if (blockState.getValue(MarkerSwitchBlock.OPEN) && tileEntity.getLevel()
                    .getBlockState(tileEntity.getBlockPos()).getValue(MarkerSwitchBlock.HALF) == DoubleBlockHalf.LOWER
                    && tileEntity.hasItem()) {
                ItemStack itemStack = tileEntity.getStackInSlot(0);
                poseStack.pushPose();
                double y = (double) (Mth.sin((tileEntity.getLevel().getGameTime() % 80) / 80.0F * (float) Math.PI * 2.0F) / 20.0D) + 0.8D;
                poseStack.translate(0.5D, y, 0.5D);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.rotateAround(new Quaternionf().rotationY(((float) (tileEntity.getLevel().getGameTime() % 160) / 160.0F) * (float) Math.PI * 2.0F), 0.0F, 0.0F, 0.0F);
                Direction facing = tileEntity.getBlockState().getValue(MarkerSwitchBlock.FACING);
                float rotation = facing == Direction.EAST || facing == Direction.WEST ? facing.toYRot() + 45.0F
                        : facing.toYRot() - 135.0F;
                poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
                int lightAbove = LevelRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());
                this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, lightAbove, OverlayTexture.NO_OVERLAY,
                        poseStack, bufferSource, tileEntity.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }

}