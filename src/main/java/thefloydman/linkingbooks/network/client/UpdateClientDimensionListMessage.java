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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public record UpdateClientDimensionListMessage(Set<ResourceKey<Level>> newAges,
                                               Set<ResourceKey<Level>> deletedAges) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateClientDimensionListMessage> TYPE = new CustomPacketPayload.Type<>(Reference.getAsResourceLocation("update_client_dimension_list"));

    private static final Codec<ResourceKey<Level>> CODEC = Codec.of(UpdateClientDimensionListMessage::encodeLevelResourceKey, UpdateClientDimensionListMessage::decodeLevelResourceKey);

    public static final StreamCodec<ByteBuf, UpdateClientDimensionListMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(NeoForgeExtraCodecs.setOf(CODEC)), UpdateClientDimensionListMessage::newAges,
            ByteBufCodecs.fromCodec(NeoForgeExtraCodecs.setOf(CODEC)), UpdateClientDimensionListMessage::deletedAges,
            UpdateClientDimensionListMessage::new
    );

    private static <T> DataResult<T> encodeLevelResourceKey(ResourceKey<Level> resourceKey, DynamicOps<T> ops, T input) {
        return ResourceLocation.CODEC.encode(resourceKey.location(), ops, input);
    }

    private static <T> DataResult<Pair<ResourceKey<Level>, T>> decodeLevelResourceKey(DynamicOps<T> ops, T input) {
        DataResult<Pair<ResourceLocation, T>> dataResult = ResourceLocation.CODEC.decode(ops, input);
        if (dataResult.isSuccess() && dataResult.result().isPresent()) {
            ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, dataResult.result().get().getFirst());
            return new DataResult.Success<>(Pair.of(resourceKey, input), Lifecycle.stable());
        }
        return new DataResult.Error<>(() -> "Could not parse ResourceKey<Level>.", Optional.empty(), Lifecycle.stable());
    }

    public static void handle(final UpdateClientDimensionListMessage data, final IPayloadContext context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Set<ResourceKey<Level>> levels = player.connection.levels();
            levels.addAll(data.newAges());
            levels.removeAll(data.deletedAges());
        }
    }

    @Override
    public @Nonnull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
