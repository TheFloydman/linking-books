package thefloydman.linkingbooks.network;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.util.ImageUtils;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import javax.annotation.Nonnull;
import java.util.UUID;

public record SaveLinkingPanelImageMessage(NativeImage image, UUID uuid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SaveLinkingPanelImageMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "save_linking_book_screenshot"));

    public static final StreamCodec<ByteBuf, SaveLinkingPanelImageMessage> STREAM_CODEC = StreamCodec.composite(
            ImageUtils.NATIVE_IMAGE_STREAM_CODEC, SaveLinkingPanelImageMessage::image,
            UUIDUtil.STREAM_CODEC, SaveLinkingPanelImageMessage::uuid,
            SaveLinkingPanelImageMessage::new
    );

    @Override
    public @Nonnull CustomPacketPayload.Type<SaveLinkingPanelImageMessage> type() {
        return TYPE;
    }

    public static void handle(final SaveLinkingPanelImageMessage data, final IPayloadContext context) {

        try {
            LinkingBooksSavedData worldData = context.player().getServer().getLevel(Level.OVERWORLD).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
            worldData.addLinkingPanelImage(data.uuid(), data.image());
        } catch (NullPointerException exception) {
            LogUtils.getLogger().warn(exception.getMessage());
        }

    }

}
