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
package thefloydman.linkingbooks.util;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.ModCapabilities;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.inventory.container.LinkingBookContainer;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.network.packets.TakeScreenshotForLinkingBookMessage;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

public class LinkingUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemStack createWrittenLinkingBook(Player player, ItemStack originItem) {

        ItemStack resultItem = ModItems.GREEN_WRITTEN_LINKING_BOOK.get().getDefaultInstance();

        String itemName = ForgeRegistries.ITEMS.getKey(originItem.getItem()).getPath();

        if (itemName.equals(Reference.ItemNames.BLACK_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.BLACK_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.BLUE_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.BLUE_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.BROWN_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.BROWN_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.CYAN_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.CYAN_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.GRAY_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.GRAY_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.LIGHT_BLUE_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.LIGHT_BLUE_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.LIGHT_GRAY_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.LIGHT_GRAY_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.LIME_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.LIME_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.MAGENTA_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.MAGENTA_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.ORANGE_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.ORANGE_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.PINK_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.PINK_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.PURPLE_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.PURPLE_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.RED_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.RED_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else if (itemName.equals(Reference.ItemNames.WHITE_BLANK_LINKING_BOOK)) {
            resultItem = ModItems.WHITE_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        } else {
            resultItem = ModItems.YELLOW_WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        }

        ILinkData linkData = resultItem.getCapability(ModCapabilities.LINK_DATA).orElse(null);
        if (linkData == null) {
            return ItemStack.EMPTY;
        }
        linkData.setDimension(player.getCommandSenderWorld().dimension().location());
        linkData.setPosition(player.blockPosition());
        linkData.setRotation(player.getYRot());

        ModNetworkHandler.sendToPlayer(new TakeScreenshotForLinkingBookMessage(linkData.getUUID()),
                (ServerPlayer) player);

        return resultItem;
    }

    /**
     * Teleport an entity to a dimension and position. Should only be called
     * server-side.
     */
    public static boolean linkEntity(Entity entity, ILinkData linkData, boolean holdingBook) {

        Level world = entity.getCommandSenderWorld();

        if (world.isClientSide()) {
            LOGGER.info(
                    "An attempt has been made to directly link an entity from the client. Only do this from the server.");
        } else if (linkData == null) {
            LOGGER.info("A null ILinkInfo has been supplied. Link failed.");
        } else if (linkData.getDimension() == null) {
            LOGGER.info("ILinkData.getDimension() returned null. Link failed.");
        } else if (linkData.getPosition() == null) {
            LOGGER.info("ILinkData.getPosition() returned null. Link failed.");
        } else if (!linkData.getLinkEffectsAsRL().contains(new ResourceLocation("linkingbooks:intraage_linking"))
                && world.dimension().location().equals(linkData.getDimension())) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                player.closeContainer();
                player.doCloseContainer();
                player.displayClientMessage(new TranslatableComponent("message.linkingbooks.no_intraage_linking"),
                        true);
            }
        } else {

            ServerLevel serverWorld = world.getServer()
                    .getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, linkData.getDimension()));

            if (serverWorld == null) {
                LOGGER.info("Cannot find dimension \"" + linkData.getDimension().toString() + "\". Link failed.");
                return false;
            }

            Set<LinkEffect> linkEffects = linkData.getLinkEffectsAsLE();

            for (LinkEffect effect : linkEffects) {
                if (!effect.canStartLink(entity, linkData)) {
                    if (entity instanceof ServerPlayer) {
                        ServerPlayer player = (ServerPlayer) entity;
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(new TranslatableComponent("message.linkingbooks.link_failed_start"),
                                true);
                    }
                    return false;
                }
            }

            for (LinkEffect effect : linkEffects) {
                effect.onLinkStart(entity, linkData);
            }

            Vec3 originalPos = entity.position();
            float originalRot = entity.getYRot();
            BlockPos pos = linkData.getPosition();
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;
            float rotation = linkData.getRotation();
            boolean tookExperience = false;

            /*
             * TODO: Find a way to teleport without client moving entity model through
             * world.
             */

            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                // Deduct experience levels if a cost has been set in config.
                if (!player.isCreative()) {
                    if (player.experienceLevel < ModConfig.COMMON.linkingCostExperienceLevels.get()) {
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(
                                new TranslatableComponent("message.linkingbooks.insufficient_experience"), true);
                        return false;
                    }
                    player.giveExperienceLevels(ModConfig.COMMON.linkingCostExperienceLevels.get() * -1);
                    tookExperience = true;
                }
                if (holdingBook
                        && !linkData.getLinkEffectsAsRL().contains(Reference.getAsResourceLocation("tethered"))) {
                    LinkingBookEntity book = new LinkingBookEntity(world, player.getMainHandItem().copy());
                    Vec3 lookVec = player.getLookAngle();
                    book.setPos(player.getX() + (lookVec.x() / 4.0D), player.getY() + 1.0D,
                            player.getZ() + (lookVec.z() / 4.0D));
                    book.setYRot(player.yHeadRot);
                    world.addFreshEntity(book);
                    player.getMainHandItem().shrink(1);
                }
                player.doCloseContainer();
                player.closeContainer();
                player.teleportTo(serverWorld, x, y, z, rotation, player.getXRot());
            } else {
                CompoundTag nbt = new CompoundTag();
                entity.saveAsPassenger(nbt);
                entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
                Entity entityCopy = EntityType.create(nbt, serverWorld).orElse(null);
                if (entityCopy == null) {
                    return false;
                }
                entityCopy.setPos(x, y, z);
                serverWorld.addFreshEntity(entityCopy);
                serverWorld.addDuringTeleport(entityCopy);
            }
            for (LinkEffect effect : linkEffects) {
                if (!effect.canFinishLink(entity, linkData)) {
                    if (entity instanceof ServerPlayer) {
                        ServerPlayer player = (ServerPlayer) entity;
                        if (tookExperience) {
                            player.giveExperienceLevels(ModConfig.COMMON.linkingCostExperienceLevels.get());
                        }
                        serverWorld.getServer().execute(() -> {
                            player.teleportTo((ServerLevel) world, originalPos.x, originalPos.y, originalPos.z,
                                    originalRot, player.getXRot());
                            player.displayClientMessage(
                                    new TranslatableComponent("message.linkingbooks.link_failed_end"), true);
                        });
                    } else {
                        serverWorld.getServer().execute(() -> {
                            CompoundTag tag = new CompoundTag();
                            entity.saveAsPassenger(tag);
                            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
                            Entity entityCopy = EntityType.create(tag, world).orElse(null);
                            if (entityCopy != null) {
                                entityCopy.setPos(originalPos.x, originalPos.y, originalPos.z);
                                world.addFreshEntity(entityCopy);
                                ((ServerLevel) world).addDuringTeleport(entityCopy);
                            }
                        });
                    }
                    return false;
                }
            }
            for (LinkEffect effect : linkEffects) {
                effect.onLinkEnd(entity, linkData);
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

    public static void openLinkingBookGui(ServerPlayer player, boolean holdingBook, int color, ILinkData linkData,
            ResourceLocation currentDimension) {
        NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, playerEntity) -> {
            return new LinkingBookContainer(id, playerInventory);
        }, new TextComponent("")), extraData -> {
            extraData.writeBoolean(holdingBook);
            extraData.writeInt(color);
            linkData.write(extraData);
            boolean canLink = !currentDimension.equals(linkData.getDimension())
                    || linkData.getLinkEffectsAsRL().contains(Reference.getAsResourceLocation("intraage_linking"));
            extraData.writeBoolean(canLink);
            LinkingBooksSavedData savedData = player.getServer().getLevel(Level.OVERWORLD).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::load, LinkingBooksSavedData::new, Reference.MOD_ID);
            CompoundTag image = savedData.getLinkingPanelImage(linkData.getUUID());
            extraData.writeNbt(image);
        });
    }

}
