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

package thefloydman.linkingbooks.linking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public record LinkEffect(LinkEffectType type, BiFunction<Entity, LinkData, Boolean> canStartLink,
                         BiFunction<Entity, LinkData, Boolean> canFinishLink,
                         BiConsumer<Entity, LinkData> onLinkStart, BiConsumer<Entity, LinkData> onLinkEnd) {

    public static final ResourceKey<Registry<LinkEffect>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Reference.MODID, Reference.RegistryKeyNames.LINK_EFFECT));

    public static final Codec<LinkEffect> CODEC = Codec.of(LinkEffect::encode, LinkEffect::decode);

    private static <T> DataResult<T> encode(LinkEffect linkEffect, DynamicOps<T> ops, T prefix) {
        return new DataResult.Error<>(() -> "Writing Link Effects to disk is currently not supported.", Optional.empty(), Lifecycle.stable());
    }

    private static <T> DataResult<Pair<LinkEffect, T>> decode(DynamicOps<T> ops, T input) {

        JsonObject topLevelJsonObject = ops.convertTo(JsonOps.INSTANCE, input).getAsJsonObject();

        if (topLevelJsonObject.has("type")) {
            JsonElement typeElement = topLevelJsonObject.get("type");
            DataResult<Pair<ResourceLocation, JsonElement>> typePair = ResourceLocation.CODEC.decode(JsonOps.INSTANCE, typeElement);
            if (typePair.isSuccess() && typePair.result().isPresent()) {
                ResourceLocation typeResourceLocation = typePair.result().get().getFirst();
                LinkEffectType linkEffectType = LinkEffectTypes.REGISTRY.get(typeResourceLocation);
                if (linkEffectType != null) {
                    DataResult<Pair<LinkEffectType, T>> newPair = linkEffectType.codec().decode(ops, input);
                    if (newPair.isSuccess() && newPair.result().isPresent()) {
                        LinkEffectType specificLinkEffectType = newPair.result().get().getFirst();
                        return new DataResult.Success<>(
                                Pair.of(
                                        new LinkEffect(
                                                specificLinkEffectType,
                                                getCanStartLink(specificLinkEffectType),
                                                getCanFinishLink(specificLinkEffectType),
                                                getOnLinkStart(specificLinkEffectType),
                                                getOnLinkEnd(specificLinkEffectType)
                                        ), input),
                                Lifecycle.stable());
                    }
                }
            }
        }

        return new DataResult.Error<>(() -> "Could not parse Link Effect.", Optional.empty(), Lifecycle.stable());

    }

    private static BiFunction<Entity, LinkData, Boolean> getCanStartLink(LinkEffectType type) {
        return type::canStartLink;
    }

    private static BiFunction<Entity, LinkData, Boolean> getCanFinishLink(LinkEffectType type) {
        return type::canFinishLink;
    }

    private static BiConsumer<Entity, LinkData> getOnLinkStart(LinkEffectType type) {
        return type::onLinkStart;
    }

    private static BiConsumer<Entity, LinkData> getOnLinkEnd(LinkEffectType type) {
        return type::onLinkEnd;
    }

    public static @Nullable LinkEffect getLinkEffect(@Nonnull ResourceLocation resourceLocation) {
        Optional<Registry<LinkEffect>> optionalRegistry = Reference.server.registryAccess().registry(REGISTRY_KEY);
        if (optionalRegistry.isPresent()) {
            Registry<LinkEffect> linkEffectRegistry = optionalRegistry.get();
            return linkEffectRegistry.get(resourceLocation);
        }
        return null;
    }

}