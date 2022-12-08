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

    public final BooleanValue alwaysAllowIntraAgeLinking;
    public final IntValue linkingCostLevels;

    ModConfig(ForgeConfigSpec.Builder builder) {

        this.alwaysAllowIntraAgeLinking = builder.comment(
                "Whether to allow linking within the same dimension, even with books that don't have the IntraAge Link Effect applied.")
                .translation("linkingbooks.config.always_allow_intraage_linking")
                .define("alwaysAllowIntraAgeLinking", false);

        this.linkingCostLevels = builder
                .comment("How many experience levels it costs to use a linking book. Stacks with points option.")
                .translation("linkingbooks.config.linkingcost_levels")
                .defineInRange("linkingCostLevels", 0, 0, Integer.MAX_VALUE);

    }

}