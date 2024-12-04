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

package thefloydman.linkingbooks.world.generation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import thefloydman.linkingbooks.Reference;

import java.util.Optional;
import java.util.UUID;

public record AgeInfo(ResourceLocation id, Component name, UUID owner) {

    public static final AgeInfo DUMMY = new AgeInfo(Reference.getAsResourceLocation("dummy"), Component.translatable("age.linkingbooks.name.unnamed"), UUID.randomUUID());

    public static final Codec<AgeInfo> CODEC = RecordCodecBuilder.create(
            codecBuilderInstance -> codecBuilderInstance.group(
                            ResourceLocation.CODEC.fieldOf("id").forGetter(AgeInfo::id),
                            Codec.of(AgeInfo::encodeComponent, AgeInfo::decodeComponent).fieldOf("name").forGetter(AgeInfo::name),
                            UUIDUtil.CODEC.fieldOf("owner").forGetter(AgeInfo::owner)
                    )
                    .apply(codecBuilderInstance, AgeInfo::new)
    );

    private static <T> DataResult<T> encodeComponent(Component component, DynamicOps<T> ops, T input) {
        JsonObject topLevelJsonObject = new JsonObject();
        boolean translatable = component.getContents() instanceof TranslatableContents;
        topLevelJsonObject.addProperty("contents", translatable ? ((TranslatableContents) component.getContents()).getKey() : component.getString());
        topLevelJsonObject.addProperty("translatable", translatable);
        return ExtraCodecs.JSON.encode(topLevelJsonObject, ops, input);
    }

    private static <T> DataResult<Pair<Component, T>> decodeComponent(DynamicOps<T> ops, T input) {
        JsonObject topLevelJsonObject = ops.convertTo(JsonOps.INSTANCE, input).getAsJsonObject();
        boolean translatable = false;
        if (topLevelJsonObject.has("translatable")) {
            JsonElement translatableElement = topLevelJsonObject.get("translatable");
            DataResult<Pair<Boolean, JsonElement>> translatablePair = Codec.BOOL.decode(JsonOps.INSTANCE, translatableElement);
            if (translatablePair.isSuccess() && translatablePair.result().isPresent()) {
                translatable = translatablePair.result().get().getFirst();
            }
        }
        if (topLevelJsonObject.has("contents")) {
            JsonElement contentsElement = topLevelJsonObject.get("contents");
            DataResult<Pair<String, JsonElement>> contentsPair = Codec.STRING.decode(JsonOps.INSTANCE, contentsElement);
            if (contentsPair.isSuccess() && contentsPair.result().isPresent()) {
                Component component = translatable ? Component.translatable(contentsPair.result().get().getFirst()) : Component.literal(contentsPair.result().get().getFirst());
                return new DataResult.Success<>(Pair.of(component, input), Lifecycle.stable());
            }
        }
        return new DataResult.Error<>(() -> "Could not parse Component.", Optional.empty(), Lifecycle.stable());
    }

}