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
package thefloydman.linkingbooks.linking;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import thefloydman.linkingbooks.api.linking.LinkEffect;

/**
 * Used to load Link Effects from datapacks.
 */
public class LinkEffectManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Map<ResourceLocation, LinkEffect> linkEffects = ImmutableMap.of();

    public LinkEffectManager() {
        super(GSON, "linkeffects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager,
            ProfilerFiller profilerFiller) {

        Map<LinkEffect.Type, ImmutableMap.Builder<ResourceLocation, LinkEffect>> map = Maps.newHashMap();
        ImmutableMap.Builder<ResourceLocation, LinkEffect> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();
            try {
                ResourceLocation type = new ResourceLocation(json.get(LinkEffect.TAG_TYPE).getAsString());
                LinkEffect.Type linkEffectType = getType(type);
                if (linkEffectType == null) {
                    LOGGER.info("Skipping loading link effect {} as its type could not be found", resourcelocation);
                    continue;
                }
                LinkEffect linkEffect = linkEffectType.fromJson(json);
                if (linkEffect == null) {
                    LOGGER.info("Skipping loading link effect {} as its serializer encountered an error",
                            resourcelocation);
                    continue;
                }
                map.computeIfAbsent(linkEffectType, (p_44075_) -> {
                    return ImmutableMap.builder();
                }).put(resourcelocation, linkEffect);
                builder.put(resourcelocation, linkEffect);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading link effect {}", resourcelocation, jsonparseexception);
            }
        }
        linkEffects = builder.build();
        LOGGER.info("Loaded {} link effects", linkEffects.size());

    }

    public static LinkEffect get(ResourceLocation resourceLocation) {
        return linkEffects.get(resourceLocation);
    }

    public static ResourceLocation getKey(LinkEffect linkEffect) {
        Entry<ResourceLocation, LinkEffect> entry = linkEffects.entrySet().stream()
                .filter(foo -> foo.getValue() == linkEffect).findFirst().orElse(null);
        return entry == null ? null : entry.getKey();
    }

    /**
     * Convenience method to retrieve a Link Effect Type from the registry in
     * LinkEffect.Type.
     * 
     * @param resource The ResourceLocation of the desired Link Effect Type.
     * @return The requested Link Effect Type, or <b>null</b> if it is not
     *         available.
     */
    public static LinkEffect.Type getType(ResourceLocation resource) {
        return LinkEffect.Type.get(resource);
    }

}
