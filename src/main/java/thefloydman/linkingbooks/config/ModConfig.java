package thefloydman.linkingbooks.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
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

    ModConfig(ForgeConfigSpec.Builder builder) {

        this.linkingCostExperiencePoints = builder
                .comment("How many experience points it costs to use a linking book. Stacks with levels option.")
                .translation("linkingbooks.configgui.linkingcost_points")
                .defineInRange("linkingCostPoints", 0, 0, Integer.MAX_VALUE);

        this.linkingCostExperienceLevels = builder
                .comment("How many experience levels it costs to use a linking book. Stacks with points option.")
                .translation("linkingbooks.configgui.linkingcost_levels")
                .defineInRange("linkingCostLevels", 0, 0, Integer.MAX_VALUE);

    }

}