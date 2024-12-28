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

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import thefloydman.linkingbooks.Reference;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LinkingBooksDimensionFactory {

    public static LevelStem createDimension(MinecraftServer server, ResourceKey<LevelStem> levelStemKey,
                                            ResourceKey<DimensionType> dimensionType) {
        List<ResourceKey<Biome>> biomes = Lists.newArrayList(Biomes.JUNGLE, Biomes.DESERT);
        Holder<NoiseGeneratorSettings> noiseGeneratorSettings = server.registryAccess()
                .registryOrThrow(Registries.NOISE_SETTINGS).getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
        MultiNoiseBiomeSourceParameterList.Preset preset = new MultiNoiseBiomeSourceParameterList.Preset(
                Reference.getAsResourceLocation("overworld_like"),
                new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
                    @Override
                    public @Nonnull <T> Climate.ParameterList<T> apply(@Nonnull Function<ResourceKey<Biome>, T> valueGetter) {
                        return new Climate.ParameterList<>(
                                biomes.stream().map(biome -> Pair.of(
                                        Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                        valueGetter.apply(biome)
                                )).toList()
                        );
                    }
                });
        HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = server.registryAccess().lookupOrThrow(Registries.BIOME);
        MultiNoiseBiomeSourceParameterList parameterList = new MultiNoiseBiomeSourceParameterList(preset, biomeRegistryLookup);
        MultiNoiseBiomeSource multiNoiseBiomeSource = MultiNoiseBiomeSource.createFromPreset(Holder.direct(parameterList));
        ChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(multiNoiseBiomeSource, noiseGeneratorSettings);
        return new LevelStem(getDimensionTypeHolder(server, dimensionType), chunkGenerator);
    }

    public static LevelStem createRelto(MinecraftServer server, ResourceKey<LevelStem> levelStemKey,
                                        ResourceKey<DimensionType> dimensionType) {
        Optional<Holder.Reference<Biome>> biomeReference = server.registryAccess().lookupOrThrow(Registries.BIOME).get(ModBiomes.RELTO);
        Holder<Biome> biomeHolder = biomeReference.orElseGet(() -> server.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.THE_VOID));
        ChunkGenerator chunkGenerator = new PrefabChunkGenerator(biomeHolder);
        return new LevelStem(getDimensionTypeHolder(server, dimensionType), chunkGenerator);
    }

    public static Holder<DimensionType> getDimensionTypeHolder(MinecraftServer server,
                                                               ResourceKey<DimensionType> dimensionType) {
        return server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(dimensionType);
    }

}
