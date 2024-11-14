package thefloydman.linkingbooks.linking;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import thefloydman.linkingbooks.data.LinkData;

import javax.annotation.Nonnull;

public interface LinkEffectType {

    public <T extends LinkEffectType> @Nonnull Codec<T> codec();

    public @Nonnull ResourceLocation typeID();

    /**
     * Fires before entity changes dimensions and before onLinkStart has been called
     * for any LinkEffect.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     * @return Whether the link should proceed. If false, entity will not link.
     */
    public default boolean canStartLink(Entity entity, LinkData linkData) {
        return true;
    }

    /**
     * Fires after entity changes dimensions and onLinkStart has been called for
     * every LinkEffect but before onLinkEnd has been called for any LinkEffect.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     * @return Whether the link should proceed successfully. If false, entity will
     * be returned to origin.
     */
    public default boolean canFinishLink(Entity entity, LinkData linkData) {
        return true;
    }

    /**
     * Fires before entity changes dimensions.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     */
    public default void onLinkStart(Entity entity, LinkData linkData) {
    }

    /**
     * Fires after entity changes dimensions.
     *
     * @param entity   The Entity that is linking.
     * @param linkData The LinkDataComponent for the link.
     */
    public default void onLinkEnd(Entity entity, LinkData linkData) {
    }

}