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
package thefloydman.linkingbooks.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ModConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ModConfig COMMON;
    static {
        final Pair<ModConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig::new);
        SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public final IntValue linkingCostExperiencePoints;
    public final IntValue linkingCostExperienceLevels;
    public final IntValue linkingPanelChunkLoadRadius;
    public final IntValue linkingPanelChunkRenderDistance;
    public final BooleanValue useImmersivePortalsForLinkingPanels;
    public final BooleanValue useImmersivePortalsForLinkingPortals;

    ModConfig(ForgeConfigSpec.Builder builder) {

        this.linkingCostExperiencePoints = builder
                .comment("How many experience points it costs to use a linking book. Stacks with levels option.")
                .translation("linkingbooks.configgui.linkingcost_points")
                .defineInRange("linkingCostPoints", 0, 0, Integer.MAX_VALUE);

        this.linkingCostExperienceLevels = builder
                .comment("How many experience levels it costs to use a linking book. Stacks with points option.")
                .translation("linkingbooks.configgui.linkingcost_levels")
                .defineInRange("linkingCostLevels", 0, 0, Integer.MAX_VALUE);

        this.linkingPanelChunkLoadRadius = builder
                .comment("The radius of chunks to load when a linking panel is rendering.")
                .translation("linkingbooks.configgui.panel_chunk_load_radius")
                .defineInRange("linkingPanelChunkLoadRadius", 4, 0, Integer.MAX_VALUE);

        this.linkingPanelChunkRenderDistance = builder
                .comment("The maximum render distance (in chunks) of a linking panel.")
                .translation("linkingbooks.configgui.panel_chunk_render_distance")
                .defineInRange("linkingPanelChunkRenderDistance", 4, 0, Integer.MAX_VALUE);

        this.useImmersivePortalsForLinkingPanels = builder
                .comment("If Immersive Portals is present, uses it to render Linking Panels.")
                .translation("linkingbooks.configgui.use_immersive_portals_for_linking_panels")
                .define("useImmersivePortalsForLinkingPanels", true);

        this.useImmersivePortalsForLinkingPortals = builder
                .comment("If Immersive Portals is present, uses it to render Linking Portals.")
                .translation("linkingbooks.configgui.use_immersive_portals_for_linking_portals")
                .define("useImmersivePortalsForLinkingPortals", true);

    }

}