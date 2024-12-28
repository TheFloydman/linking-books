/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
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
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record UpdatePlayerDisplayNames(Map<UUID, String> displayNames) implements CustomPacketPayload {

    public static final Type<UpdatePlayerDisplayNames> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "update_player_display_names"));

    public static final StreamCodec<ByteBuf, UpdatePlayerDisplayNames> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ByteBufCodecs.STRING_UTF8), UpdatePlayerDisplayNames::displayNames,
            UpdatePlayerDisplayNames::new
    );

    public static void handle(final UpdatePlayerDisplayNames data, final IPayloadContext context) {

        context.enqueueWork(() -> {
            Reference.PLAYER_DISPLAY_NAMES.putAll(data.displayNames());
        });

    }

    @Override
    public @Nonnull Type<UpdatePlayerDisplayNames> type() {
        return TYPE;
    }

}
