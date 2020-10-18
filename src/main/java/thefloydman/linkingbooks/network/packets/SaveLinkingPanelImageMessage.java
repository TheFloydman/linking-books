package thefloydman.linkingbooks.network.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.util.Reference;

public class SaveLinkingPanelImageMessage implements IMessage {

    private NativeImage image = new NativeImage(64, 42, false);
    private UUID uuid;

    public SaveLinkingPanelImageMessage(NativeImage image, UUID uuid) {
        if (image != null) {
            this.image = image;
        }
        this.uuid = uuid;
    }

    public SaveLinkingPanelImageMessage() {
        this(null, UUID.randomUUID());
    }

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
        try {
            LogManager.getLogger(Reference.MOD_ID).info(this.image.getBytes().length);
            buffer.writeByteArray(this.image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();

        if (RenderSystem.isOnRenderThread()) {
            ByteBuffer buf = ByteBuffer.allocate(buffer.readableBytes());
            buf.put(buffer.readByteArray());
            try {
                this.image = NativeImage.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            RenderSystem.recordRenderCall(() -> {
                ByteBuffer buf = ByteBuffer.allocate(buffer.readableBytes());
                buf.put(buffer.readByteArray());
                try {
                    this.image = NativeImage.read(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            LogManager.getLogger(Reference.MOD_ID).info("Received screenshot on server");
            this.image.close();
            ctx.setPacketHandled(true);
        });
    }

}
