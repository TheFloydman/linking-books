/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2025 Dan Floyd ("TheFloydman").
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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.world.generation.AgeInfo;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public record UpdateClientAgeInfoMapMessage(Map<ResourceLocation, AgeInfo> ageInfos) implements CustomPacketPayload {

    public static final Type<UpdateClientAgeInfoMapMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "update_client_age_info_map"));

    public static final StreamCodec<ByteBuf, UpdateClientAgeInfoMapMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, AgeInfo.STREAM_CODEC), UpdateClientAgeInfoMapMessage::ageInfos,
            UpdateClientAgeInfoMapMessage::new
    );

    public static void handle(final UpdateClientAgeInfoMapMessage data, final IPayloadContext context) {

        context.enqueueWork(() -> Reference.AGE_INFO_MAP.putAll(data.ageInfos()));

    }

    @Override
    public @Nonnull Type<UpdateClientAgeInfoMapMessage> type() {
        return TYPE;
    }

}
