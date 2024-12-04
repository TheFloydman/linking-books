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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;

public record AddChunkLoaderMessage(LinkData linkData) implements CustomPacketPayload {

    public static final Type<AddChunkLoaderMessage> TYPE = new Type<>(Reference.getAsResourceLocation("add_chunk_loader"));

    public static final StreamCodec<ByteBuf, AddChunkLoaderMessage> STREAM_CODEC = StreamCodec.composite(
            LinkData.STREAM_CODEC, AddChunkLoaderMessage::linkData,
            AddChunkLoaderMessage::new
    );

    public static void handle(final AddChunkLoaderMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> ImmersivePortalsIntegration.addChunkLoader(data.linkData, (ServerPlayer) context.player()));
    }

    @Override
    public @Nonnull Type<AddChunkLoaderMessage> type() {
        return TYPE;
    }

}