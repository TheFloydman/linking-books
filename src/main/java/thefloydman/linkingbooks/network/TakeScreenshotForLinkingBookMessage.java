package thefloydman.linkingbooks.network;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.DeltaTracker;
import net.minecraft.core.UUIDUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;
import java.util.UUID;

public record TakeScreenshotForLinkingBookMessage(UUID uuid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TakeScreenshotForLinkingBookMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "take_linking_book_screenshot"));

    public static final StreamCodec<ByteBuf, TakeScreenshotForLinkingBookMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TakeScreenshotForLinkingBookMessage::uuid, TakeScreenshotForLinkingBookMessage::new
    );

    @Override
    public @Nonnull CustomPacketPayload.Type<TakeScreenshotForLinkingBookMessage> type() {
        return TYPE;
    }

    public static void handle(final TakeScreenshotForLinkingBookMessage data, final IPayloadContext context) {

        if (RenderSystem.isOnRenderThread()) {
            getScreenshot(data, context);
        } else {
            RenderSystem.recordRenderCall(() -> getScreenshot(data, context));
        }

    }

    public static void getScreenshot(final TakeScreenshotForLinkingBookMessage data, final IPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget buffer = mc.getMainRenderTarget();
        float largeWidth = buffer.viewWidth;
        float largeHeight = buffer.viewHeight;
        float smallWidth = 64.0F;
        float smallHeight = 42.0F;
        if (largeWidth / largeHeight > smallWidth / smallHeight) {
            while (largeHeight % 42 != 0) {
                largeHeight--;
            }
            while (largeWidth / largeHeight != smallWidth / smallHeight) {
                largeWidth--;
            }
        } else if (largeWidth / largeHeight < smallWidth / smallHeight) {
            while (largeWidth % 64 != 0) {
                largeWidth--;
            }
            while (largeWidth / largeHeight != smallWidth / smallHeight) {
                largeHeight--;
            }
        }

        NativeImage fullImage = new NativeImage(buffer.width, buffer.height, false);
        mc.getMainRenderTarget().bindWrite(true);
        boolean hide = mc.options.hideGui;
        mc.options.hideGui = true;
        mc.gameRenderer.renderLevel(DeltaTracker.ONE);
        mc.options.hideGui = hide;
        buffer.bindRead();
        fullImage.downloadTexture(0, true);
        mc.getMainRenderTarget().unbindWrite();
        fullImage.flipY();
        NativeImage largeImage = new NativeImage((int) largeWidth, (int) largeHeight, false);
        int initialX = (int) ((buffer.viewWidth - largeWidth) / 2);
        int initialY = (int) ((buffer.viewHeight - largeHeight) / 2);
        for (int largeY = 0, fullY = initialY; largeY < largeHeight; largeY++, fullY++) {
            for (int largeX = 0, fullX = initialX; largeX < largeWidth; largeX++, fullX++) {
                largeImage.setPixelRGBA(largeX, largeY, fullImage.getPixelRGBA(fullX, fullY));
            }
        }
        NativeImage smallImage = new NativeImage((int) smallWidth, (int) smallHeight, false);
        largeImage.resizeSubRectTo(0, 0, (int) largeWidth, (int) largeHeight, smallImage);
        largeImage.close();
        fullImage.close();

        context.enqueueWork(() -> {
            PacketDistributor.sendToServer(new SaveLinkingPanelImageMessage(smallImage, data.uuid()));
        });

    }

}
