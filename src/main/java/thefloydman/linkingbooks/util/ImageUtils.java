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

package thefloydman.linkingbooks.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.render.MyRenderHelper;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ImageUtils {

    public static final Codec<NativeImage> NATIVE_IMAGE_CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            Codec.INT.fieldOf("width").forGetter(NativeImage::getWidth),
                            Codec.INT.fieldOf("height").forGetter(NativeImage::getHeight),
                            Codec.INT_STREAM.fieldOf("pixels").forGetter(image -> {
                                List<Integer> pixels = new ArrayList<>();
                                for (int y = 0; y < image.getHeight(); y++) {
                                    for (int x = 0; x < image.getWidth(); x++) {
                                        pixels.add(image.getPixelRGBA(x, y));
                                    }
                                }
                                return pixels.stream().mapToInt(Integer::intValue);
                            })
                    )
                    .apply(codecBuilderInstance, (width, height, intStream) -> {
                        int[] pixels = intStream.toArray();
                        NativeImage image = new NativeImage(width, height, false);
                        int i = 0;
                        for (int y = 0; (y < height) && (i < pixels.length); y++) {
                            for (int x = 0; x < width && i < pixels.length; x++, i++) {
                                image.setPixelRGBA(x, y, pixels[i]);
                            }
                        }
                        return image;
                    })
    );

    /**
     * Copies a block of pixels from the read RenderTarget to the draw RenderTarget, centering the cropped rectangle in the read RenderTarget.
     *
     * @param readRenderTarget       the RenderTarget that will be read from
     * @param drawRenderTarget       the RenderTarget that will be drawn to
     * @param width                  the width of the source rectangle within the read RenderTarget
     * @param height                 the height of the source rectangle within the read RenderTarget
     * @param copyColor              whether top copy color between RenderTargets
     * @param copyDepth              whether top copy depth between RenderTargets
     * @param copyStencil            whether top copy stencil between RenderTargets
     * @param useLinearInterpolation if copying color ONLY, whether to used Linear interpolation when shrinking; otherwise, Nearest Neighbor will be used
     */
    public static void cropShrinkCenterRenderTarget(RenderTarget readRenderTarget, RenderTarget drawRenderTarget, int width, int height, boolean copyColor, boolean copyDepth, boolean copyStencil, boolean useLinearInterpolation) {
        int scale = Mth.floor(Math.min((float) readRenderTarget.width / (float) width, (float) readRenderTarget.height / (float) height));
        int cropWidth = width * scale;
        int cropHeight = height * scale;
        int cropX = (int) (((float) readRenderTarget.width - (float) cropWidth) / 2.0F);
        int cropY = (int) (((float) readRenderTarget.height - (float) cropHeight) / 2.0F);
        cropShrinkRenderTarget(readRenderTarget, drawRenderTarget, cropX, cropY, cropWidth, cropHeight, copyColor, copyDepth, copyStencil, useLinearInterpolation);
    }

    /**
     * Copies a block of pixels from the read RenderTarget to the draw RenderTarget.
     *
     * @param readRenderTarget       the RenderTarget that will be read from
     * @param drawRenderTarget       the RenderTarget that will be drawn to
     * @param x                      the x-coordinate of the top-left corner of the source rectangle within the read RenderTarget
     * @param y                      the y-coordinate of the top-left corner of the source rectangle within the read RenderTarget
     * @param width                  the width of the source rectangle within the read RenderTarget
     * @param height                 the height of the source rectangle within the read RenderTarget
     * @param copyColor              whether top copy color between RenderTargets
     * @param copyDepth              whether top copy depth between RenderTargets
     * @param copyStencil            whether top copy stencil between RenderTargets
     * @param useLinearInterpolation if copying color ONLY, whether to used Linear interpolation when shrinking; otherwise, Nearest Neighbor will be used
     */
    public static void cropShrinkRenderTarget(RenderTarget readRenderTarget, RenderTarget drawRenderTarget, int x, int y, int width, int height, boolean copyColor, boolean copyDepth, boolean copyStencil, boolean useLinearInterpolation) {
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readRenderTarget.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawRenderTarget.frameBufferId);
        GlStateManager._glBlitFrameBuffer(
                x, y, x + width, y + height,
                0, 0, drawRenderTarget.width, drawRenderTarget.height,
                (copyColor ? GL11.GL_COLOR_BUFFER_BIT : 0) | (copyDepth ? GL11.GL_DEPTH_BUFFER_BIT : 0) | (copyStencil ? GL11.GL_STENCIL_BUFFER_BIT : 0),
                useLinearInterpolation && copyColor && !copyDepth && !copyStencil ? GL11.GL_LINEAR : GL11.GL_NEAREST
        );
    }

    public static void drawRenderTarget(RenderTarget renderTarget, boolean doUseAlphaBlend, boolean doEnableModifyAlpha, int left, int top, int viewportWidth, int viewportHeight) {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        CHelper.checkGlError();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.viewport(left, Minecraft.getInstance().getMainRenderTarget().height - viewportHeight - top, viewportWidth, viewportHeight);
        if (doUseAlphaBlend) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        } else {
            RenderSystem.disableBlend();
        }

        RenderSystem.colorMask(true, true, true, doEnableModifyAlpha);

        ShaderInstance shader = doUseAlphaBlend ? Minecraft.getInstance().gameRenderer.blitShader : MyRenderHelper.blitScreenNoBlendShader;
        Validate.notNull(shader, "shader is null");
        shader.setSampler("DiffuseSampler", renderTarget.getColorTextureId());
        shader.apply();
        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferBuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());
        shader.clear();
        RenderSystem.depthMask(true);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        CHelper.checkGlError();
    }
}