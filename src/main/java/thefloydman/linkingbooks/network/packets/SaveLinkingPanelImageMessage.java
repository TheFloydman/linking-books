package thefloydman.linkingbooks.network.packets;

import java.util.UUID;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksGlobalSavedData;

public class SaveLinkingPanelImageMessage implements IMessage {

    private static final int MAX_WIDTH = 64;
    private static final int MAX_HEIGHT = 42;
    private NativeImage image = new NativeImage(MAX_WIDTH, MAX_HEIGHT, false);
    private UUID uuid;

    public SaveLinkingPanelImageMessage(NativeImage image, UUID uuid) {
        if (image != null) {
            if (image.getWidth() > MAX_WIDTH && image.getHeight() > MAX_HEIGHT) {
                NativeImage smallerImage = new NativeImage(MAX_WIDTH, MAX_HEIGHT, false);
                for (int ySmall = 0, yLarge = (image.getHeight() - smallerImage.getHeight()) / 2; ySmall < smallerImage
                        .getHeight(); ySmall++, yLarge++) {
                    for (int xSmall = 0, xLarge = (image.getWidth() - smallerImage.getWidth())
                            / 2; xSmall < smallerImage.getWidth(); xSmall++, xLarge++) {
                        smallerImage.setPixelRGBA(xSmall, ySmall, image.getPixelRGBA(xLarge, yLarge));
                    }
                }
                this.image = smallerImage;
            } else {
                this.image = image;
            }
        }
        this.uuid = uuid;
    }

    public SaveLinkingPanelImageMessage() {
        this(null, UUID.randomUUID());
    }

    @Override
    public PacketBuffer toData(PacketBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
        buffer.writeInt(this.image.getHeight());
        buffer.writeInt(this.image.getWidth());
        for (int y = 0; y < this.image.getHeight(); y++) {
            for (int x = 0; x < this.image.getWidth(); x++) {
                buffer.writeInt(this.image.getPixelRGBA(x, y));
            }
        }
        return buffer;
    }

    @Override
    public void fromData(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        int height = buffer.readInt();
        int width = buffer.readInt();
        this.image = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.image.setPixelRGBA(x, y, buffer.readInt());
            }
        }
    }

    @Override
    public void handle(Context ctx) {
        ctx.enqueueWork(() -> {
            LinkingBooksGlobalSavedData worldData = ctx.getSender().getServer().getWorld(World.field_234918_g_)
                    .getSavedData().getOrCreate(LinkingBooksGlobalSavedData::new, Reference.MOD_ID);
            worldData.addLinkingPanelImage(this.uuid, this.image);
            ctx.setPacketHandled(true);
        });
    }

}
