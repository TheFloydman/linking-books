package thefloydman.linkingbooks.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.item.ModItems;

public class LinkingUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final Map<Item, Item> BLANK_TO_WRITTEN = new HashMap<Item, Item>() {
        {
            put(ModItems.BLACK_BLANK_LINKING_BOOK.get(), ModItems.BLACK_WRITTEN_LINKING_BOOK.get());
        }
    };

    public static ItemStack createWrittenLinkingBook(PlayerEntity player, Item blankItem) {
        Item writtenItem = BLANK_TO_WRITTEN.get(blankItem);
        if (writtenItem == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(writtenItem);
        ILinkData capability = stack.getCapability(LinkData.LINK_DATA).orElse(null);
        if (capability == null) {
            return ItemStack.EMPTY;
        }
        capability.setDimension(player.getEntityWorld().func_234923_W_().func_240901_a_());
        capability.setPosition(player.func_233580_cy_());
        capability.setRotation(player.rotationYaw);
        return stack;
    }

    /**
     * Teleport an entity to a dimension and position. Should only be called
     * server-side.
     */
    public static boolean linkEntity(Entity entity, ILinkData linkInfo) {

        World world = entity.getEntityWorld();

        if (world.isRemote()) {
            LOGGER.info(
                    "An attempt has been made to directly link an entity from the client. Only do this from the server.");
        } else if (linkInfo == null) {
            LOGGER.info("An null ILinkInfo has been supplied. Link failed.");
        } else if (linkInfo.getDimension() == null) {
            LOGGER.info("ILinkInfo::getDimension returned null. Link failed.");
        } else if (linkInfo.getPosition() == null) {
            LOGGER.info("ILinkInfo::getPosition returned null. Link failed.");
        } else {

            ServerWorld serverWorld = world.getServer()
                    .getWorld(RegistryKey.func_240903_a_(Registry.field_239699_ae_, linkInfo.getDimension()));

            if (serverWorld == null) {
                LOGGER.info("Cannot find dimension \"" + linkInfo.getDimension().toString() + "\". Link failed.");
                return false;
            }

            BlockPos pos = linkInfo.getPosition();
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            float rotation = linkInfo.getRotation();

            /*
             * TODO: Find a way to teleport without client moving entity model through
             * world.
             */

            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                player.teleport(serverWorld, x, y, z, rotation, player.rotationPitch);
                for (LinkEffect effect : linkInfo.getLinkEffects()) {
                    effect.onLinkEnd(player);
                }
            } else {
                entity.func_241206_a_(serverWorld);
                entity.teleportKeepLoaded(x, y, z);
            }
            return true;
        }
        return false;
    }

    /**
     * Teleport multiple entities to a dimension and position using the same
     * ILinkInfo. Should only be called server-side.
     * 
     * @param entities
     * @param linkInfo
     * @return The number of entities that were successfully teleported.
     */
    public static int linkEntities(List<Entity> entities, ILinkData linkInfo) {
        int linked = 0;
        for (Entity entity : entities) {
            linked += linkEntity(entity, linkInfo) == true ? 1 : 0;
        }
        return linked;
    }

}