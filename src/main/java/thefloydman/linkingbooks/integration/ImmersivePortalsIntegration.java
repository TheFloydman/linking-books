/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.qouteall.immersive_portals.chunk_loading.ChunkVisibilityManager;
import com.qouteall.immersive_portals.chunk_loading.ChunkVisibilityManager.ChunkLoader;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.chunk_loading.NewChunkTrackingGraph;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.config.ModConfig;

public class ImmersivePortalsIntegration {

    private static Map<UUID, ChunkLoader> chunkLoaders = new HashMap<UUID, ChunkLoader>();

    public static void addChunkLoader(ILinkData linkData, ServerPlayerEntity player) {
        removeChunkLoader(linkData, player);
        ChunkLoader chunkLoader = new ChunkVisibilityManager.ChunkLoader(
                new DimensionalChunkPos(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, linkData.getDimension()),
                        new ChunkPos(linkData.getPosition())),
                ModConfig.COMMON.linkingPanelChunkLoadRadius.get());
        chunkLoaders.put(linkData.getUUID(), chunkLoader);
        NewChunkTrackingGraph.addPerPlayerAdditionalChunkLoader(player, chunkLoader);
    }

    public static void removeChunkLoader(ILinkData linkData, ServerPlayerEntity player) {
        ChunkLoader chunkLoader = chunkLoaders.remove(linkData.getUUID());
        if (chunkLoader != null) {
            NewChunkTrackingGraph.removePerPlayerAdditionalChunkLoader(player, chunkLoader);
        }
    }

}