/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2019-2024 Dan Floyd ("TheFloydman").
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

    public static final ModConfigSpec CONFIG = BUILDER.build();

}