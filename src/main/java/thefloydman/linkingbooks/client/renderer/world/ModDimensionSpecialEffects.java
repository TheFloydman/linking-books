/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2025 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.client.renderer.world;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.ReflectionHelper;
import thefloydman.linkingbooks.world.generation.AgeInfo;
import thefloydman.linkingbooks.world.generation.CloudInfo;
import thefloydman.linkingbooks.world.sky.SkyObject;
import thefloydman.linkingbooks.world.sky.SkyUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ModDimensionSpecialEffects {

    public static final Function<LevelRenderer, VertexBuffer> SKY_BUFFER
            = ReflectionHelper.getFieldGetter(LevelRenderer.class, "skyBuffer");
    public static final Function<LevelRenderer, VertexBuffer> STAR_BUFFER
            = ReflectionHelper.getFieldGetter(LevelRenderer.class, "starBuffer");
    public static final Function<LevelRenderer, VertexBuffer> DARK_BUFFER
            = ReflectionHelper.getFieldGetter(LevelRenderer.class, "darkBuffer");
    public static final Function<LevelRenderer, Boolean> GENERATE_CLOUDS_GETTER
            = ReflectionHelper.getFieldGetter(LevelRenderer.class, "generateClouds");
    public static final BiConsumer<LevelRenderer, Boolean> GENERATE_CLOUDS_SETTER
            = ReflectionHelper.getFieldSetter(LevelRenderer.class, "generateClouds");

    private static final ResourceLocation CLOUDS_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");

    @OnlyIn(Dist.CLIENT)
    public static class AgeEffects extends DimensionSpecialEffects {

        private final Map<CloudInfo, Vector3i> prevCloudPosMap = new HashMap<>();
        private final Map<CloudInfo, CloudStatus> prevCloudStatusMap = new HashMap<>();
        private final Map<CloudInfo, Vec3> prevCloudColorMap = new HashMap<>();
        private final Map<CloudInfo, VertexBuffer> cloudBuffers = new HashMap<>();

        public AgeEffects() {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, true);
        }

        @Override
        public @Nonnull Vec3 getBrightnessDependentFogColor(@Nonnull Vec3 p_108901_, float p_108902_) {
            return p_108901_;
        }

        @Override
        public boolean isFoggyAt(int x, int y) {
            return false;
        }

        @Override
        public boolean renderSky(@Nonnull ClientLevel level, int ticks, float partialTicks, @Nonnull Matrix4f frustumMatrix, @Nonnull Camera camera, @Nonnull Matrix4f projectionMatrix, boolean isFoggy, @Nonnull Runnable setupFog) {

            AgeInfo ageInfo = Reference.AGE_INFO_MAP.get(level.dimension().location());
            if (ageInfo == null) {
                return false;
            }

            Minecraft minecraft = Minecraft.getInstance();
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(frustumMatrix);

            RenderSystem.depthMask(false);
            ShaderInstance shaderInstance = RenderSystem.getShader();
            if (shaderInstance == null) {
                return true;
            }

            if (ageInfo.overrideBiomeSkyColor()) {
                renderSkyMain(poseStack, ageInfo.skyColor());
            } else {
                Vec3 vec3 = level.getSkyColor(minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
                Color skyColor = new Color((float) vec3.x, (float) vec3.y, (float) vec3.z);
                renderSkyMain(poseStack, skyColor.getRGB());
            }

            renderSkyObjects(ageInfo.skyObject(), level.getDayTime(), poseStack);

            RenderSystem.defaultBlendFunc();

            RenderSystem.depthMask(true);

            return true;

        }

        private void renderSkyObjects(SkyObject mainSkyObject, long time, PoseStack poseStack) {
            List<SkyObjectDetails> allSkyObjectDetails = SkyUtils.generateSkyObjectDetails(mainSkyObject, time);
            for (SkyObjectDetails skyObjectDetails : allSkyObjectDetails) {
                RenderSystem.enableBlend();
                int color = skyObjectDetails.getSkyObject().hasColor() ? skyObjectDetails.getSkyObject().color() : Color.WHITE.getRGB();
                float radius = skyObjectDetails.getSkyObject().apparentRadius();
                float y = 100.0F;

                // Glow
                poseStack.pushPose();
                poseStack.mulPose(skyObjectDetails.getQuaternion());
                Matrix4f matrix4f = poseStack.last().pose();
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE
                );

                ResourceLocation glowLocation = ResourceLocation.fromNamespaceAndPath(skyObjectDetails.getSkyObject().texture().getNamespace(), skyObjectDetails.getSkyObject().texture().getPath() + "_glow_black.png");
                RenderSystem.setShaderTexture(0, glowLocation);
                BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferbuilder.addVertex(matrix4f, -radius, y, -radius).setUv(0.0F, 0.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, radius, y, -radius).setUv(1.0F, 0.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, radius, y, radius).setUv(1.0F, 1.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, -radius, y, radius).setUv(0.0F, 1.0F).setColor(color);
                BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

                // Body
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ZERO
                );
                ResourceLocation bodyLocation = ResourceLocation.fromNamespaceAndPath(skyObjectDetails.getSkyObject().texture().getNamespace(), skyObjectDetails.getSkyObject().texture().getPath() + "_body_trans.png");
                RenderSystem.setShaderTexture(0, bodyLocation);
                bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferbuilder.addVertex(matrix4f, -radius, y, -radius).setUv(0.0F, 0.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, radius, y, -radius).setUv(1.0F, 0.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, radius, y, radius).setUv(1.0F, 1.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, -radius, y, radius).setUv(0.0F, 1.0F).setColor(color);
                BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
                poseStack.popPose();

                RenderSystem.disableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        private void renderSkyMain(PoseStack poseStack, int color) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tesselator tesselator = Tesselator.getInstance();

            for (int i = 0; i < 6; i++) {
                poseStack.pushPose();
                if (i == 1) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                }

                if (i == 2) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                }

                if (i == 3) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                }

                if (i == 4) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                }

                if (i == 5) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                }

                Matrix4f matrix4f = poseStack.last().pose();
                BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                bufferbuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setColor(color);
                bufferbuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setColor(color);
                BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
                poseStack.popPose();
            }

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }

        /**
         * Altered copy of {@link net.minecraft.client.renderer.LevelRenderer#renderClouds(PoseStack, Matrix4f, Matrix4f, float, double, double, double)  LevelRenderer.renderClouds()}
         */
        @Override
        public boolean renderClouds(ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, double camX, double camY, double camZ, @Nonnull Matrix4f frustumMatrix, @Nonnull Matrix4f projectionMatrix) {

            AgeInfo ageInfo = Reference.AGE_INFO_MAP.get(level.dimension().location());
            if (ageInfo == null) {
                return false;
            }

            int tickOffset = 0;
            float speed = 0.02F;
            double zOffset = 0.0D;
            for (CloudInfo cloudInfo : ageInfo.cloudInfos()) {
                if (!Float.isNaN(cloudInfo.height())) {
                    Minecraft minecraft = Minecraft.getInstance();
                    LevelRenderer levelRenderer = minecraft.levelRenderer;
                    float f1 = 12.0F;
                    float f2 = 4.0F;
                    double d0 = 0.0002D;
                    double d1 = ((float) (ticks += tickOffset) + partialTick) * speed;
                    double d2 = (camX + d1) / 12.0D;
                    double d3 = cloudInfo.height() - (float) camY + 0.33F;
                    double d4 = (camZ / 12.0D) + 0.33D + zOffset;
                    d2 -= Mth.floor(d2 / 2048.0D) * 2048.0D;
                    d4 -= Mth.floor(d4 / 2048.0D) * 2048.0D;
                    float f3 = (float) (d2 - (double) Mth.floor(d2));
                    float f4 = (float) (d3 / 4.0D - (double) Mth.floor(d3 / 4.0D)) * 4.0F;
                    float f5 = (float) (d4 - (double) Mth.floor(d4));
                    Color color = new Color(cloudInfo.color());
                    Vec3 vec3 = new Vec3(color.getRed() / 255.0D, color.getGreen() / 255.0D, color.getBlue() / 255.0D); // TODO: Alter based on time of day.
                    int i = (int) Math.floor(d2);
                    int j = (int) Math.floor(d3 / 4.0D);
                    int k = (int) Math.floor(d4);
                    Vector3i prevCloudPos = prevCloudPosMap.getOrDefault(cloudInfo, new Vector3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
                    CloudStatus prevCloudsType = prevCloudStatusMap.getOrDefault(cloudInfo, minecraft.options.getCloudsType());
                    Vec3 prevCloudColor = prevCloudColorMap.getOrDefault(cloudInfo, Vec3.ZERO);
                    if (i != prevCloudPos.x() || j != prevCloudPos.y() || k != prevCloudPos.z() || minecraft.options.getCloudsType() != prevCloudsType || prevCloudColor.distanceToSqr(vec3) > 2.0E-4) {
                        prevCloudPosMap.put(cloudInfo, new Vector3i(i, j, k));
                        prevCloudColorMap.put(cloudInfo, vec3);
                        prevCloudStatusMap.put(cloudInfo, minecraft.options.getCloudsType());
                        GENERATE_CLOUDS_SETTER.accept(levelRenderer, true);
                    }

                    if (GENERATE_CLOUDS_GETTER.apply(levelRenderer)) {
                        GENERATE_CLOUDS_SETTER.accept(levelRenderer, false);
                        VertexBuffer cloudBuffer = this.cloudBuffers.get(cloudInfo);
                        if (cloudBuffer != null) {
                            cloudBuffer.close();
                        }

                        this.cloudBuffers.put(cloudInfo, new VertexBuffer(VertexBuffer.Usage.STATIC));
                        cloudBuffer = this.cloudBuffers.get(cloudInfo);
                        cloudBuffer.bind();
                        cloudBuffer.upload(this.buildClouds(Tesselator.getInstance(), d2, d3, d4, vec3, prevCloudStatusMap.getOrDefault(cloudInfo, Minecraft.getInstance().options.getCloudsType())));
                        VertexBuffer.unbind();
                    }

                    FogRenderer.levelFogColor();
                    poseStack.pushPose();
                    poseStack.mulPose(frustumMatrix);
                    poseStack.scale(12.0F, 1.0F, 12.0F);
                    poseStack.translate(-f3, f4, -f5);
                    VertexBuffer cloudBuffer = this.cloudBuffers.get(cloudInfo);
                    if (cloudBuffer != null) {
                        cloudBuffer.bind();
                        int l = prevCloudStatusMap.getOrDefault(cloudInfo, minecraft.options.getCloudsType()) == CloudStatus.FANCY ? 0 : 1;

                        for (int i1 = l; i1 < 2; i1++) {
                            RenderType renderType = i1 == 0 ? RenderType.cloudsDepthOnly() : RenderType.clouds();
                            renderType.setupRenderState();
                            ShaderInstance shaderInstance = RenderSystem.getShader();
                            if (shaderInstance != null) {
                                cloudBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance);
                            }
                            renderType.clearRenderState();
                        }

                        VertexBuffer.unbind();
                    }

                    poseStack.popPose();
                    tickOffset += 2000;
                    speed += 0.02F;
                    zOffset += 97.0D; // Largest prime number under 100.
                }
            }

            return true;
        }

        /**
         * Copy of {@link net.minecraft.client.renderer.LevelRenderer#buildClouds(Tesselator, double, double, double, Vec3) LevelRenderer.buildClouds()}
         */
        private MeshData buildClouds(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor, CloudStatus cloudStatus) {
            float f = 4.0F;
            float f1 = 0.00390625F;
            int i = 8;
            int j = 4;
            float f2 = 9.765625E-4F;
            float f3 = (float) Mth.floor(x) * 0.00390625F;
            float f4 = (float) Mth.floor(z) * 0.00390625F;
            float f5 = (float) cloudColor.x;
            float f6 = (float) cloudColor.y;
            float f7 = (float) cloudColor.z;
            float f8 = f5 * 0.9F;
            float f9 = f6 * 0.9F;
            float f10 = f7 * 0.9F;
            float f11 = f5 * 0.7F;
            float f12 = f6 * 0.7F;
            float f13 = f7 * 0.7F;
            float f14 = f5 * 0.8F;
            float f15 = f6 * 0.8F;
            float f16 = f7 * 0.8F;
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
            float f17 = (float) Math.floor(y / 4.0) * 4.0F;
            if (cloudStatus == CloudStatus.FANCY) {
                for (int k = -3; k <= 4; k++) {
                    for (int l = -3; l <= 4; l++) {
                        float f18 = (float) (k * 8);
                        float f19 = (float) (l * 8);
                        if (f17 > -5.0F) {
                            bufferbuilder.addVertex(f18 + 0.0F, f17 + 0.0F, f19 + 8.0F)
                                    .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                    .setColor(f11, f12, f13, 0.8F)
                                    .setNormal(0.0F, -1.0F, 0.0F);
                            bufferbuilder.addVertex(f18 + 8.0F, f17 + 0.0F, f19 + 8.0F)
                                    .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                    .setColor(f11, f12, f13, 0.8F)
                                    .setNormal(0.0F, -1.0F, 0.0F);
                            bufferbuilder.addVertex(f18 + 8.0F, f17 + 0.0F, f19 + 0.0F)
                                    .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                    .setColor(f11, f12, f13, 0.8F)
                                    .setNormal(0.0F, -1.0F, 0.0F);
                            bufferbuilder.addVertex(f18 + 0.0F, f17 + 0.0F, f19 + 0.0F)
                                    .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                    .setColor(f11, f12, f13, 0.8F)
                                    .setNormal(0.0F, -1.0F, 0.0F);
                        }

                        if (f17 <= 5.0F) {
                            bufferbuilder.addVertex(f18 + 0.0F, f17 + 4.0F - 9.765625E-4F, f19 + 8.0F)
                                    .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                    .setColor(f5, f6, f7, 0.8F)
                                    .setNormal(0.0F, 1.0F, 0.0F);
                            bufferbuilder.addVertex(f18 + 8.0F, f17 + 4.0F - 9.765625E-4F, f19 + 8.0F)
                                    .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                    .setColor(f5, f6, f7, 0.8F)
                                    .setNormal(0.0F, 1.0F, 0.0F);
                            bufferbuilder.addVertex(f18 + 8.0F, f17 + 4.0F - 9.765625E-4F, f19 + 0.0F)
                                    .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                    .setColor(f5, f6, f7, 0.8F)
                                    .setNormal(0.0F, 1.0F, 0.0F);
                            bufferbuilder.addVertex(f18 + 0.0F, f17 + 4.0F - 9.765625E-4F, f19 + 0.0F)
                                    .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                    .setColor(f5, f6, f7, 0.8F)
                                    .setNormal(0.0F, 1.0F, 0.0F);
                        }

                        if (k > -1) {
                            for (int i1 = 0; i1 < 8; i1++) {
                                bufferbuilder.addVertex(f18 + (float) i1 + 0.0F, f17 + 0.0F, f19 + 8.0F)
                                        .setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(-1.0F, 0.0F, 0.0F);
                                bufferbuilder.addVertex(f18 + (float) i1 + 0.0F, f17 + 4.0F, f19 + 8.0F)
                                        .setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(-1.0F, 0.0F, 0.0F);
                                bufferbuilder.addVertex(f18 + (float) i1 + 0.0F, f17 + 4.0F, f19 + 0.0F)
                                        .setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(-1.0F, 0.0F, 0.0F);
                                bufferbuilder.addVertex(f18 + (float) i1 + 0.0F, f17 + 0.0F, f19 + 0.0F)
                                        .setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(-1.0F, 0.0F, 0.0F);
                            }
                        }

                        if (k <= 1) {
                            for (int j2 = 0; j2 < 8; j2++) {
                                bufferbuilder.addVertex(f18 + (float) j2 + 1.0F - 9.765625E-4F, f17 + 0.0F, f19 + 8.0F)
                                        .setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(1.0F, 0.0F, 0.0F);
                                bufferbuilder.addVertex(f18 + (float) j2 + 1.0F - 9.765625E-4F, f17 + 4.0F, f19 + 8.0F)
                                        .setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(1.0F, 0.0F, 0.0F);
                                bufferbuilder.addVertex(f18 + (float) j2 + 1.0F - 9.765625E-4F, f17 + 4.0F, f19 + 0.0F)
                                        .setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(1.0F, 0.0F, 0.0F);
                                bufferbuilder.addVertex(f18 + (float) j2 + 1.0F - 9.765625E-4F, f17 + 0.0F, f19 + 0.0F)
                                        .setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4)
                                        .setColor(f8, f9, f10, 0.8F)
                                        .setNormal(1.0F, 0.0F, 0.0F);
                            }
                        }

                        if (l > -1) {
                            for (int k2 = 0; k2 < 8; k2++) {
                                bufferbuilder.addVertex(f18 + 0.0F, f17 + 4.0F, f19 + (float) k2 + 0.0F)
                                        .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, -1.0F);
                                bufferbuilder.addVertex(f18 + 8.0F, f17 + 4.0F, f19 + (float) k2 + 0.0F)
                                        .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, -1.0F);
                                bufferbuilder.addVertex(f18 + 8.0F, f17 + 0.0F, f19 + (float) k2 + 0.0F)
                                        .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, -1.0F);
                                bufferbuilder.addVertex(f18 + 0.0F, f17 + 0.0F, f19 + (float) k2 + 0.0F)
                                        .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, -1.0F);
                            }
                        }

                        if (l <= 1) {
                            for (int l2 = 0; l2 < 8; l2++) {
                                bufferbuilder.addVertex(f18 + 0.0F, f17 + 4.0F, f19 + (float) l2 + 1.0F - 9.765625E-4F)
                                        .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, 1.0F);
                                bufferbuilder.addVertex(f18 + 8.0F, f17 + 4.0F, f19 + (float) l2 + 1.0F - 9.765625E-4F)
                                        .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, 1.0F);
                                bufferbuilder.addVertex(f18 + 8.0F, f17 + 0.0F, f19 + (float) l2 + 1.0F - 9.765625E-4F)
                                        .setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, 1.0F);
                                bufferbuilder.addVertex(f18 + 0.0F, f17 + 0.0F, f19 + (float) l2 + 1.0F - 9.765625E-4F)
                                        .setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4)
                                        .setColor(f14, f15, f16, 0.8F)
                                        .setNormal(0.0F, 0.0F, 1.0F);
                            }
                        }
                    }
                }
            } else {
                int j1 = 1;
                int k1 = 32;

                for (int l1 = -32; l1 < 32; l1 += 32) {
                    for (int i2 = -32; i2 < 32; i2 += 32) {
                        bufferbuilder.addVertex((float) (l1 + 0), f17, (float) (i2 + 32))
                                .setUv((float) (l1 + 0) * 0.00390625F + f3, (float) (i2 + 32) * 0.00390625F + f4)
                                .setColor(f5, f6, f7, 0.8F)
                                .setNormal(0.0F, -1.0F, 0.0F);
                        bufferbuilder.addVertex((float) (l1 + 32), f17, (float) (i2 + 32))
                                .setUv((float) (l1 + 32) * 0.00390625F + f3, (float) (i2 + 32) * 0.00390625F + f4)
                                .setColor(f5, f6, f7, 0.8F)
                                .setNormal(0.0F, -1.0F, 0.0F);
                        bufferbuilder.addVertex((float) (l1 + 32), f17, (float) (i2 + 0))
                                .setUv((float) (l1 + 32) * 0.00390625F + f3, (float) (i2 + 0) * 0.00390625F + f4)
                                .setColor(f5, f6, f7, 0.8F)
                                .setNormal(0.0F, -1.0F, 0.0F);
                        bufferbuilder.addVertex((float) (l1 + 0), f17, (float) (i2 + 0))
                                .setUv((float) (l1 + 0) * 0.00390625F + f3, (float) (i2 + 0) * 0.00390625F + f4)
                                .setColor(f5, f6, f7, 0.8F)
                                .setNormal(0.0F, -1.0F, 0.0F);
                    }
                }
            }

            return bufferbuilder.buildOrThrow();
        }

    }

}
