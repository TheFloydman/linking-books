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

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PrefabChunkGenerator extends ChunkGenerator {

    public PrefabChunkGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
    }

    @Override
    protected @Nonnull MapCodec<? extends ChunkGenerator> codec() {
        return NoiseBasedChunkGenerator.CODEC;
    }

    @Override
    public void applyCarvers(@Nonnull WorldGenRegion worldGenRegion, long p_223044_, @Nonnull RandomState randomState, @Nonnull BiomeManager biomeManager,
                             @Nonnull StructureManager structureManager, @Nonnull ChunkAccess chunkAccess, @Nonnull Carving carving) {
    }

    @Override
    public void buildSurface(@Nonnull WorldGenRegion worldGenRegion, @Nonnull StructureManager structureManager, @Nonnull RandomState randomState,
                             @Nonnull ChunkAccess chunkAccess) {
    }

    @Override
    public void spawnOriginalMobs(@Nonnull WorldGenRegion worldGenRegion) {
    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public @Nonnull CompletableFuture<ChunkAccess> fillFromNoise(@Nonnull Blender blender, @Nonnull RandomState randomState, @Nonnull StructureManager structureManager, @Nonnull ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int p_223032_, int p_223033_, @Nonnull Types types, @Nonnull LevelHeightAccessor levelHeightAccessor,
                             @Nonnull RandomState randomState) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int p_223028_, int p_223029_, @Nonnull LevelHeightAccessor levelHeightAccessor,
                                     @Nonnull RandomState randomState) {
        return null;
    }

    @Override
    public void addDebugScreenInfo(@Nonnull List<String> p_223175_, @Nonnull RandomState randomState, @Nonnull BlockPos blockPos) {
    }

}