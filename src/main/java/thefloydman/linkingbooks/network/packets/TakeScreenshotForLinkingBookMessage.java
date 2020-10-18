package thefloydman.linkingbooks.network.packets;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.util.Reference;

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
        int backupWidth = buffer.framebufferWidth;
        int backupHeight = buffer.framebufferHeight;
        buffer.resize(192, 126, false);
        NativeImage image = new NativeImage(buffer.framebufferWidth, buffer.framebufferHeight, false);
        buffer.bindFramebufferTexture();
        image.downloadFromTexture(0, true);
        image.flip();
        buffer.resize(backupWidth, backupHeight, false);

        ModNetworkHandler.sendToServer(new SaveLinkingPanelImageMessage(image, this.uuid));

        File folder = Minecraft.getInstance().gameDir;
        // file.mkdir();
        final File file2 = new File(folder, this.uuid.toString() + ".png");
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                image.write(file2);
            } catch (IOException e) {
                LogManager.getLogger(Reference.MOD_ID).info("Could not save linking panel image to client.");
                e.printStackTrace();
            } finally {
                image.close();
            }
        } else {
            LogManager.getLogger(Reference.MOD_ID)
                    .info("Could not save linking panel image to client. File already exists.");
        }
    }

}
