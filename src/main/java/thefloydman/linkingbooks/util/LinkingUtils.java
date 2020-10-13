package thefloydman.linkingbooks.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.inventory.container.LinkingBookContainer;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.linking.LinkEffects;

public class LinkingUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemStack createWrittenLinkingBook(PlayerEntity player, ItemStack originItem) {

        ItemStack resultItem = ModItems.WRITTEN_LINKING_BOOK.get().getDefaultInstance();

        ILinkData linkData = resultItem.getCapability(LinkData.LINK_DATA).orElse(null);
        if (linkData == null) {
            return ItemStack.EMPTY;
        }
        linkData.setDimension(player.getEntityWorld().func_234923_W_().func_240901_a_());
        linkData.setPosition(player.func_233580_cy_());
        linkData.setRotation(player.rotationYaw);

        IColorCapability originColor = originItem.getCapability(ColorCapability.COLOR).orElse(null);
        IColorCapability resultColor = resultItem.getCapability(ColorCapability.COLOR).orElse(null);
        if (originColor == null || resultColor == null) {
            return ItemStack.EMPTY;
        }
        resultColor.setColor(originColor.getColor());

        return resultItem;
    }

    /**
     * Teleport an entity to a dimension and position. Should only be called
     * server-side.
     */
    public static boolean linkEntity(Entity entity, ILinkData linkInfo, boolean holdingBook) {

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
        } else if (!linkInfo.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING.get())
                && world.func_234923_W_().func_240901_a_().equals(linkInfo.getDimension())) {
            if (entity instanceof PlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                player.closeScreen();
                player.closeContainer();
                /* TODO: Localize message. */
                player.sendStatusMessage(new TranslationTextComponent("message.linkingbooks.no_intraage_linking"),
                        true);
            }
        } else {

            ServerWorld serverWorld = world.getServer()
                    .getWorld(RegistryKey.func_240903_a_(Registry.field_239699_ae_, linkInfo.getDimension()));

            if (serverWorld == null) {
                LOGGER.info("Cannot find dimension \"" + linkInfo.getDimension().toString() + "\". Link failed.");
                return false;
            }

            BlockPos pos = linkInfo.getPosition();
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;
            float rotation = linkInfo.getRotation();

            /*
             * TODO: Find a way to teleport without client moving entity model through
             * world.
             */

            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            if (entity instanceof ServerPlayerEntity) {
                if (holdingBook) {
                    LinkingBookEntity book = new LinkingBookEntity(world, player.getHeldItemMainhand().copy());
                    Vector3d lookVec = player.getLookVec();
                    book.setPosition(player.getPosX() + (lookVec.getX() / 4.0D), player.getPosY() + 1.0D,
                            player.getPosZ() + (lookVec.getZ() / 4.0D));
                    book.rotationYaw = player.rotationYawHead;
                    world.addEntity(book);
                    player.getHeldItemMainhand().shrink(1);
                }
                player.closeContainer();
                player.closeScreen();
                player.teleport(serverWorld, x, y, z, rotation, player.rotationPitch);
                for (LinkEffect effect : linkInfo.getLinkEffects()) {
                    effect.onLinkEnd(player);
                }
                // Deduct experience points/levels if a cost has been set in config.
                player.giveExperiencePoints((ModConfig.COMMON.linkingCostExperiencePoints.get()
                        + ModConfig.COMMON.linkingCostExperienceLevels.get()) * -1);
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
    public static int linkEntities(List<Entity> entities, ILinkData linkInfo, boolean holdingBook) {
        int linked = 0;
        for (Entity entity : entities) {
            linked += linkEntity(entity, linkInfo, holdingBook) == true ? 1 : 0;
        }
        return linked;
    }

    public static void openLinkingBookGui(ServerPlayerEntity player, boolean holdingBook, int color,
            ILinkData linkData) {
        NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> {
            return new LinkingBookContainer(id, playerInventory);
        }, new StringTextComponent("")), extraData -> {
            extraData.writeBoolean(holdingBook);
            extraData.writeInt(color);
            linkData.write(extraData);
        });
    }

}
