package thefloydman.linkingbooks.api.linking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class LinkEffect extends ForgeRegistryEntry<LinkEffect> {

    public static final Logger LOGGER = LogManager.getLogger();

    public void onLinkStart() {
    }

    public void onLinkEnd(ServerPlayerEntity player) {
    }

    /**
     * Convenience method that retrieves a LinkEffect from the appropriate registry.
     */
    public static LinkEffect getFromResourceLocation(ResourceLocation resource) {
        IForgeRegistry<LinkEffect> registry = GameRegistry.findRegistry(LinkEffect.class);
        if (registry == null) {
            LOGGER.info("Cannot find LinkEffect registry. Returning null LinkEffect.");
            return null;
        }
        return registry.getValue(resource);
    }

}
