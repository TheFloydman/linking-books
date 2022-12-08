/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.network;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
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

    public static void sendToPlayer(IMessage msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

}
