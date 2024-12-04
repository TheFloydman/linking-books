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

package thefloydman.linkingbooks.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;

public record PlayOwnLinkingSoundMessage() implements CustomPacketPayload {

    public static final Type<PlayOwnLinkingSoundMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "play_own_linking_sound"));

    public static final StreamCodec<ByteBuf, PlayOwnLinkingSoundMessage> STREAM_CODEC = StreamCodec.unit(new PlayOwnLinkingSoundMessage());

    public static void handle(final PlayOwnLinkingSoundMessage data, final IPayloadContext context) {
        context.player().playSound(ModSounds.LINK.get());
    }

    @Override
    public @Nonnull Type<PlayOwnLinkingSoundMessage> type() {
        return TYPE;
    }

}
