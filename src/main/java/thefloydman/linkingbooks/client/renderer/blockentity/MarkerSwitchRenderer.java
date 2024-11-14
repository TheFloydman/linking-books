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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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
                poseStack.translate(0.5D, 0.6D, 0.5D);
                poseStack.scale(0.5F, 0.5F, 0.5F);
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