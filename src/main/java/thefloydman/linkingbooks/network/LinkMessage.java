package thefloydman.linkingbooks.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;

public record LinkMessage(LinkData linkData, boolean holdingBook) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LinkMessage> TYPE = new CustomPacketPayload.Type<>(Reference.getAsResourceLocation("link_entity"));

    public static final StreamCodec<ByteBuf, LinkMessage> STREAM_CODEC = StreamCodec.composite(
            LinkData.STREAM_CODEC, LinkMessage::linkData,
            ByteBufCodecs.BOOL, LinkMessage::holdingBook,
            LinkMessage::new
    );

    @Override
    public @Nonnull CustomPacketPayload.Type<LinkMessage> type() {
        return TYPE;
    }

    public static void handle(final LinkMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            LinkingUtils.linkEntity(context.player(), data.linkData(), data.holdingBook());
        });
    }

}