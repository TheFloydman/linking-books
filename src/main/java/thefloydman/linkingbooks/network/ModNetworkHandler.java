package thefloydman.linkingbooks.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import thefloydman.linkingbooks.network.packets.IMessage;
import thefloydman.linkingbooks.network.packets.LinkMessage;
import thefloydman.linkingbooks.network.packets.SaveLinkingPanelImageMessage;
import thefloydman.linkingbooks.network.packets.TakeScreenshotForLinkingBookMessage;
import thefloydman.linkingbooks.util.Reference;

public class ModNetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void registerAllMessages() {
        int id = 0;
        registerMessage(id++, LinkMessage.class, LinkMessage::new);
        registerMessage(id++, TakeScreenshotForLinkingBookMessage.class, TakeScreenshotForLinkingBookMessage::new);
        registerMessage(id++, SaveLinkingPanelImageMessage.class, SaveLinkingPanelImageMessage::new);
    }

    private static <MSG extends IMessage> void registerMessage(int id, Class<MSG> clazz, Supplier<MSG> supplier) {
        CHANNEL.registerMessage(id, clazz, (msg, buf) -> msg.toData(buf), (buf) -> {
            final MSG msg = supplier.get();
            msg.fromData(buf);
            return msg;
        }, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> msg.handle(ctx.get()));
        });
    }

    public static void sendToServer(IMessage msg) {
        CHANNEL.sendToServer(msg);
    }

    public static void sendToPlayer(IMessage msg, ServerPlayerEntity player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

}
