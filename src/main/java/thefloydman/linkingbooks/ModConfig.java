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

    private static final ModConfigSpec.BooleanValue ALWAYS_ALLOW_INTRAAGE_LINKING = BUILDER
            .comment("Whether to allow linking within the same dimension, even with books that don't have the IntraAge Link Effect applied.")
            .translation("linkingbooks.config.always_allow_intraage_linking")
            .define("alwaysAllowIntraAgeLinking", false);

    private static final ModConfigSpec.IntValue LINKING_COST_LEVELS = BUILDER
            .comment("How many experience levels it costs to use a linking book. Stacks with points option.")
            .translation("linkingbooks.config.linkingcost_levels")
            .defineInRange("linkingCostLevels", 0, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

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
