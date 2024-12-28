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

package thefloydman.linkingbooks;

import net.neoforged.neoforge.common.ModConfigSpec;

public class LinkingBooksConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ALWAYS_ALLOW_INTRAAGE_LINKING = BUILDER
            .comment("Whether to allow linking within the same dimension, even with books that don't have the IntraAge Link Effect applied.")
            .translation("linkingbooks.config.always_allow_intraage_linking")
            .define("alwaysAllowIntraAgeLinking", false);

    public static final ModConfigSpec.IntValue LINKING_COST_LEVELS = BUILDER
            .comment("How many experience levels it costs to use a linking book. Stacks with points option.")
            .translation("linkingbooks.config.linkingcost_levels")
            .defineInRange("linkingCostLevels", 0, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue GIVE_GUIDEBOOK_ON_FIRST_JOIN = BUILDER
            .comment("Whether to give the guidebook to every player when they first join.")
            .translation("linkingbooks.config.give_guidebook_on_first_join")
            .define("giveGuidebookOnFirstJoin", true);

    public static final ModConfigSpec.BooleanValue GIVE_RELTO_BOOK_ON_FIRST_JOIN = BUILDER
            .comment("Whether to give a Relto book to every player when they first join.")
            .translation("linkingbooks.config.give_relto_book_on_first_join")
            .define("giveReltoBookOnFirstJoin", true);

    public static final ModConfigSpec.IntValue LINKING_PANEL_CHUNK_LOAD_RADIUS = BUILDER
            .comment("The maximum render distance (in chunks) of a linking panel.")
            .translation("linkingbooks.config.linking_panel_chunk_load_radius")
            .defineInRange("linkingPanelChunkRenderDistance", 4, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue USE_IP_FOR_LINKING_PANELS = BUILDER
            .comment("If Immersive Portals is present, uses it to render Linking Portals.")
            .translation("linkingbooks.configgui.use_immersive_portals_for_linking_portals")
            .define("useImmersivePortalsForLinkingPortals", true);

    public static final ModConfigSpec CONFIG = BUILDER.build();

}