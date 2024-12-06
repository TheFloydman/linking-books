/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.network.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.network.server.SaveLinkingPanelImageMessage;
import thefloydman.linkingbooks.client.ImageUtils;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;
import java.util.UUID;

public record TakeScreenshotForLinkingBookMessage(UUID uuid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TakeScreenshotForLinkingBookMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "take_linking_book_screenshot"));

    public static final StreamCodec<ByteBuf, TakeScreenshotForLinkingBookMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TakeScreenshotForLinkingBookMessage::uuid, TakeScreenshotForLinkingBookMessage::new
    );

    public static void handle(final TakeScreenshotForLinkingBookMessage data, final IPayloadContext context) {

        if (RenderSystem.isOnRenderThread()) {
            getScreenshot(data, context);
        } else {
            RenderSystem.recordRenderCall(() -> getScreenshot(data, context));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void getScreenshot(final TakeScreenshotForLinkingBookMessage data, final IPayloadContext context) {

        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainRenderTarget = mc.getMainRenderTarget();

        boolean wasGuiHidden = mc.options.hideGui;
        mc.options.hideGui = true;
        mainRenderTarget.bindWrite(false);
        mc.gameRenderer.renderLevel(DeltaTracker.ONE);
        mc.options.hideGui = wasGuiHidden;

        TextureTarget smallerRenderTarget = new TextureTarget(64, 42, true, false);
        ImageUtils.cropShrinkCenterRenderTarget(
                mainRenderTarget,
                smallerRenderTarget,
                smallerRenderTarget.width,
                smallerRenderTarget.height,
                true,
                false,
                false,
                true
        );

        NativeImage screenshotImage = Screenshot.takeScreenshot(smallerRenderTarget);

        ImageUtils.NATIVE_IMAGE_CODEC.encodeStart(NbtOps.INSTANCE, screenshotImage).ifSuccess(tag -> {
            if (tag instanceof CompoundTag compoundTag) {
                PacketDistributor.sendToServer(new SaveLinkingPanelImageMessage(compoundTag, data.uuid()));
            }
        });

    }

    @Override
    public @Nonnull CustomPacketPayload.Type<TakeScreenshotForLinkingBookMessage> type() {
        return TYPE;
    }

}
