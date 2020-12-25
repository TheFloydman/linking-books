package thefloydman.linkingbooks.network.packets;

import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.network.ModNetworkHandler;

public class TakeScreenshotForLinkingBookMessage implements IMessage {

    private UUID uuid;

    public TakeScreenshotForLinkingBookMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public TakeScreenshotForLinkingBookMessage() {
        this(UUID.randomUUID());
    }

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {

            if (RenderSystem.isOnRenderThread()) {
                this.getScreenshot();
            } else {
                RenderSystem.recordRenderCall(() -> {
                    this.getScreenshot();
                });
            }

            ctx.setPacketHandled(true);
        });
    }

    private void getScreenshot() {

        Minecraft mc = Minecraft.getInstance();
        Framebuffer buffer = mc.getFramebuffer();
        float largeWidth = buffer.framebufferWidth;
        float largeHeight = buffer.framebufferHeight;
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

        NativeImage fullImage = new NativeImage(buffer.framebufferTextureWidth, buffer.framebufferTextureHeight, false);
        mc.getFramebuffer().bindFramebuffer(true);
        boolean hide = mc.gameSettings.hideGUI;
        mc.gameSettings.hideGUI = true;
        mc.gameRenderer.renderWorld(mc.getRenderPartialTicks(), Util.nanoTime(), new MatrixStack());
        mc.gameSettings.hideGUI = hide;
        buffer.bindFramebufferTexture();
        fullImage.downloadFromTexture(0, true);
        mc.getFramebuffer().unbindFramebuffer();
        fullImage.flip();
        NativeImage largeImage = new NativeImage((int) largeWidth, (int) largeHeight, false);
        int initialX = (int) ((buffer.framebufferWidth - largeWidth) / 2);
        int initialY = (int) ((buffer.framebufferHeight - largeHeight) / 2);
        for (int largeY = 0, fullY = initialY; largeY < largeHeight; largeY++, fullY++) {
            for (int largeX = 0, fullX = initialX; largeX < largeWidth; largeX++, fullX++) {
                largeImage.setPixelRGBA(largeX, largeY, fullImage.getPixelRGBA(fullX, fullY));
            }
        }
        NativeImage smallImage = new NativeImage((int) smallWidth, (int) smallHeight, false);
        largeImage.resizeSubRectTo(0, 0, (int) largeWidth, (int) largeHeight, smallImage);
        largeImage.close();
        fullImage.close();

        ModNetworkHandler.sendToServer(new SaveLinkingPanelImageMessage(smallImage, this.uuid));
    }

}
