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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.function.TriFunction;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.ReflectionHelper;
import thefloydman.linkingbooks.network.client.UpdateClientDimensionListMessage;
import thefloydman.linkingbooks.world.sky.SkyObject;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import java.util.*;
import java.util.concurrent.Executor;

public class AgeUtils {

    public static Pair<ServerLevel, Boolean> getOrCreateLevel(MinecraftServer server, ResourceKey<Level> levelKey, Component name, UUID owner, boolean overwriteBiomeSkyColor, int skyColor, int fogColor, SkyObject skyObject, List<CloudInfo> cloudInfos,
                                                              TriFunction<MinecraftServer, ResourceKey<LevelStem>, ResourceKey<DimensionType>, LevelStem> levelStemFactory) {

        @SuppressWarnings("deprecation")
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();

        if (map.containsKey(levelKey)) {
            return Pair.of(map.get(levelKey), false);
        } else {
            return Pair.of(createAndRegisterLevel(server, levelKey, name, owner, overwriteBiomeSkyColor, skyColor, fogColor, skyObject, cloudInfos, levelStemFactory), true);
        }
    }

    private static ServerLevel createAndRegisterLevel(MinecraftServer server, ResourceKey<Level> levelKey,
                                                      Component name, UUID owner, boolean overwriteBiomeSkyColor, int skyColor, int fogColor, SkyObject skyObject, List<CloudInfo> cloudInfos,
                                                      TriFunction<MinecraftServer, ResourceKey<LevelStem>, ResourceKey<DimensionType>, LevelStem> levelStemFactory) {

        ChunkProgressListener chunkListener = ReflectionHelper.CHUNK_PROGRESS.apply(server).create(11);
        Executor executor = ReflectionHelper.EXECUTOR.apply(server);
        LevelStorageSource.LevelStorageAccess levelStorage = ReflectionHelper.LEVEL_STORAGE.apply(server);
        DerivedLevelData derivedLevelData = new DerivedLevelData(server.getWorldData(),
                server.getWorldData().overworldData());
        LevelStem levelStem = levelStemFactory.apply(server,
                ResourceKey.create(Registries.LEVEL_STEM, levelKey.location()), ModDimensionTypes.AGE);
        boolean isDebugWorld = server.getWorldData().isDebugWorld();
        long seed = BiomeManager.obfuscateSeed(server.getWorldData().worldGenOptions().seed());
        List<CustomSpawner> customSpawners = ImmutableList.of(); // Handle special spawns via other means
        boolean inputTickTime = false; // Only true for overworld

        ServerLevel newLevel = new ServerLevel(server, executor, levelStorage, derivedLevelData, levelKey, levelStem,
                chunkListener, isDebugWorld, seed, customSpawners, inputTickTime, null);

        server.overworld().getWorldBorder()
                .addListener(new BorderChangeListener.DelegateBorderChangeListener(newLevel.getWorldBorder()));

        AgeInfo ageInfo = new AgeInfo(AgeInfo.SCHEMA_VERSION, levelKey.location(), name, owner, overwriteBiomeSkyColor, skyColor, fogColor, skyObject, cloudInfos);
        registerAge(levelKey, ageInfo, newLevel);

        NeoForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));

        return newLevel;
    }

    @SuppressWarnings("deprecation")
    private static void registerAge(ResourceKey<Level> levelKey, AgeInfo ageInfo, ServerLevel serverLevel) {
        MinecraftServer server = serverLevel.getServer();

        // Update Minecraft's level map
        server.forgeGetWorldMap().put(levelKey, serverLevel);

        // Save dimension to main SavedData so it can be recognized when game restarts.
        LinkingBooksSavedData savedData = server.overworld().getDataStorage()
                .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
        savedData.addAge(ageInfo);

        // Send dimension changes to all clients so that command suggestions display
        // correctly.
        PacketDistributor.sendToAllPlayers(new UpdateClientDimensionListMessage(ImmutableSet.of(levelKey), ImmutableSet.of()));

        // Update Forge's level cache (so level ticks)
        server.markWorldsDirty();
    }

    public static void updateAgeInfosOnStartup(MinecraftServer server) {
        LinkingBooksSavedData savedData = server.overworld().getDataStorage()
                .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
        Set<AgeInfo> updatedAges = new HashSet<>();
        for (AgeInfo ageInfo : savedData.ages) {
            AgeInfo updated = ageInfo.updateVersion();
            updatedAges.add(updated);
        }
        savedData.ages.clear();
        savedData.ages.addAll(updatedAges);
        savedData.setDirty();
    }

    public static void mapLevelsOnStartup(MinecraftServer server) {
        LinkingBooksSavedData savedData = server.overworld().getDataStorage()
                .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
        for (AgeInfo ageInfo : savedData.ages) {
            createAndRegisterLevel(server, ResourceKey.create(Registries.DIMENSION, ageInfo.id()), ageInfo.name(),
                    ageInfo.owner(), ageInfo.overrideBiomeSkyColor(), ageInfo.skyColor(), ageInfo.fogColor(), ageInfo.skyObject(), ageInfo.cloudInfos(), ageInfo.id().getPath().startsWith("relto") ? LinkingBooksDimensionFactory::createRelto : LinkingBooksDimensionFactory::createDimension);
        }
    }

}