package thefloydman.linkingbooks.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ModConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ModConfig CONFIG;
    static {
        final Pair<ModConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig::new);
        SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public final IntValue linkingcost;

    ModConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("General options to tweak your Linking Books experience.");
        this.linkingcost = builder.comment("How many experience points (not levels) it costs to use a linking book.")
                .translation("linkingbooks.configgui.linkingcost")
                .defineInRange("linkingcost", 0, 0, Integer.MAX_VALUE);
    }

}
