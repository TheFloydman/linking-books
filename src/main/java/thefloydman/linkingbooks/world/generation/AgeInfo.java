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

package thefloydman.linkingbooks.world.generation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.world.sky.SkyObject;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record AgeInfo(
        int version,
        ResourceLocation id,
        Component name,
        UUID owner,
        boolean overrideBiomeSkyColor,
        int skyColor,
        int fogColor,
        @Nonnull SkyObject skyObject,
        List<CloudInfo> cloudInfos
) {

    /**
     * Make sure to update {@link AgeInfo#updateVersion()} if you change this!
     */
    public static final int SCHEMA_VERSION = 1;

    public static final AgeInfo DUMMY = new AgeInfo(
            SCHEMA_VERSION,
            Reference.getAsResourceLocation("dummy"),
            Component.translatable("age.linkingbooks.name.unnamed"),
            UUID.randomUUID(),
            false,
            8103167,
            8103167,
            SkyObject.DUMMY,
            List.of()
    );

    public static final Codec<AgeInfo> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.optionalFieldOf("version", 0).forGetter(AgeInfo::version),
                            ResourceLocation.CODEC.optionalFieldOf("id", Reference.getAsResourceLocation("dummy")).forGetter(AgeInfo::id),
                            Codec.of(AgeInfo::encodeComponent, AgeInfo::decodeComponent).optionalFieldOf("name", Component.translatable("age.linkingbooks.name.unnamed")).forGetter(AgeInfo::name),
                            UUIDUtil.CODEC.optionalFieldOf("owner", UUID.randomUUID()).forGetter(AgeInfo::owner),
                            Codec.BOOL.optionalFieldOf("override_biome_sky_color", false).forGetter(AgeInfo::overrideBiomeSkyColor),
                            Codec.INT.optionalFieldOf("sky_color", 8103167).forGetter(AgeInfo::skyColor),
                            Codec.INT.optionalFieldOf("fog_color", 8103167).forGetter(AgeInfo::fogColor),
                            SkyObject.CODEC.optionalFieldOf("sky_object", SkyObject.DUMMY).forGetter(AgeInfo::skyObject),
                            Codec.list(CloudInfo.CODEC).optionalFieldOf("cloud_infos", List.of()).forGetter(AgeInfo::cloudInfos)
                    )
                    .apply(instance, AgeInfo::new)
    );

    public static final StreamCodec<ByteBuf, AgeInfo> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

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

    public AgeInfo(int version,
                   ResourceLocation id,
                   Component name,
                   UUID owner,
                   boolean overrideBiomeSkyColor,
                   int skyColor,
                   int fogColor,
                   @Nonnull SkyObject skyObject,
                   List<CloudInfo> cloudInfos
    ) {
        this.version = version;
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.overrideBiomeSkyColor = overrideBiomeSkyColor;
        this.skyColor = skyColor;
        this.fogColor = fogColor;
        this.skyObject = skyObject;
        this.cloudInfos = cloudInfos.stream().sorted((a, b) -> Float.compare(a.height(), b.height())).toList();
    }

    public AgeInfo updateVersion() {
        int version = this.version();
        ResourceLocation id = this.id();
        Component name = this.name();
        UUID owner = this.owner();
        boolean overrideBiomeSkyColor = this.overrideBiomeSkyColor();
        int skyColor = this.skyColor();
        int fogColor = this.fogColor();
        SkyObject skyObject = this.skyObject();
        List<CloudInfo> cloudInfos = this.cloudInfos();

        if (version == 0) {
            version = 1;
            if (id.getNamespace().equals(Reference.MODID) && id.getPath().startsWith("relto_")) {
                overrideBiomeSkyColor = false;
                skyColor = new Color(69, 7, 94).getRGB();
                fogColor = new Color(54, 42, 133).getRGB();
                SkyObject self = SkyObject.self(0, 12000L, Mth.PI / 4.0F, 12000L * 256L, 0.0F, 1.0F, List.of());
                SkyObject innerPlanet = new SkyObject(Reference.getAsResourceLocation("inner_planet"), 0, Reference.getAsResourceLocation("textures/environment/sun"), 0L, 0.0F, 24000L, 0.0F, 0.25F, 5.0F, true, new Color(182, 182, 182).getRGB(), List.of());
                skyObject = new SkyObject(Reference.getAsResourceLocation("sun"), 15, Reference.getAsResourceLocation("textures/environment/sun"), 0L, 0.0F, 0L, 0.0F, 0.0F, 30.0F, true, new Color(255, 245, 138).getRGB(), List.of(self, innerPlanet));
                cloudInfos = List.of(new CloudInfo(193.0F, new Color(191, 48, 0).getRGB()), new CloudInfo(188.0F, new Color(107, 29, 3).getRGB()), new CloudInfo(183.0F, new Color(84, 0, 0).getRGB()));
            }
        }

        if (version == 1) {
            /* Fill in when AgeInfo schema advances to 2. */
        }

        return new AgeInfo(version, id, name, owner, overrideBiomeSkyColor, skyColor, fogColor, skyObject, cloudInfos);
    }

}