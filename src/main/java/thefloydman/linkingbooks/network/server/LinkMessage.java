/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
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

package thefloydman.linkingbooks.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.linking.LinkingUtils;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;

public record LinkMessage(LinkData linkData, boolean holdingBook) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LinkMessage> TYPE = new CustomPacketPayload.Type<>(Reference.getAsResourceLocation("link_entity"));

    public static final StreamCodec<ByteBuf, LinkMessage> STREAM_CODEC = StreamCodec.composite(
            LinkData.STREAM_CODEC, LinkMessage::linkData,
            ByteBufCodecs.BOOL, LinkMessage::holdingBook,
            LinkMessage::new
    );

    public static void handle(final LinkMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            LinkingUtils.linkEntity(context.player(), data.linkData(), data.holdingBook());
        });
    }

    @Override
    public @Nonnull CustomPacketPayload.Type<LinkMessage> type() {
        return TYPE;
    }

}