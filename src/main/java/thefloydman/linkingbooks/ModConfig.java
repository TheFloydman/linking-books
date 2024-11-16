/*
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import thefloydman.linkingbooks.util.Reference;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    static final ModConfigSpec SPEC = BUILDER.build();
    private static final ModConfigSpec.BooleanValue ALWAYS_ALLOW_INTRAAGE_LINKING = BUILDER
            .comment("Whether to allow linking within the same dimension, even with books that don't have the IntraAge Link Effect applied.")
            .translation("linkingbooks.config.always_allow_intraage_linking")
            .define("alwaysAllowIntraAgeLinking", false);
    private static final ModConfigSpec.IntValue LINKING_COST_LEVELS = BUILDER
            .comment("How many experience levels it costs to use a linking book. Stacks with points option.")
            .translation("linkingbooks.config.linkingcost_levels")
            .defineInRange("linkingCostLevels", 0, 0, Integer.MAX_VALUE);
    public static boolean alwaysAllowIntraAgeLinking;
    public static int linkingCostLevels;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName
                && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        alwaysAllowIntraAgeLinking = ALWAYS_ALLOW_INTRAAGE_LINKING.get();
        linkingCostLevels = LINKING_COST_LEVELS.get();
    }
}
