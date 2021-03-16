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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
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
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.network.packets.TakeScreenshotForLinkingBookMessage;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

public class LinkingUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemStack createWrittenLinkingBook(PlayerEntity player, ItemStack originItem) {

        ItemStack resultItem = ModItems.WRITTEN_LINKING_BOOK.get().getDefaultInstance();

        ILinkData linkData = resultItem.getCapability(LinkData.LINK_DATA).orElse(null);
        if (linkData == null) {
            return ItemStack.EMPTY;
        }
        linkData.setDimension(player.getCommandSenderWorld().dimension().location());
        linkData.setPosition(player.blockPosition());
        linkData.setRotation(player.yRot);

        IColorCapability originColor = originItem.getCapability(ColorCapability.COLOR).orElse(null);
        IColorCapability resultColor = resultItem.getCapability(ColorCapability.COLOR).orElse(null);
        if (originColor == null || resultColor == null) {
            return ItemStack.EMPTY;
        }
        resultColor.setColor(originColor.getColor());

        ModNetworkHandler.sendToPlayer(new TakeScreenshotForLinkingBookMessage(linkData.getUUID()),
                (ServerPlayerEntity) player);

        return resultItem;
    }

    /**
     * Teleport an entity to a dimension and position. Should only be called
     * server-side.
     */
    public static boolean linkEntity(Entity entity, ILinkData linkData, boolean holdingBook) {

        World world = entity.getCommandSenderWorld();

        if (world.isClientSide()) {
            LOGGER.info(
                    "An attempt has been made to directly link an entity from the client. Only do this from the server.");
        } else if (linkData == null) {
            LOGGER.info("An null ILinkInfo has been supplied. Link failed.");
        } else if (linkData.getDimension() == null) {
            LOGGER.info("ILinkInfo::getDimension returned null. Link failed.");
        } else if (linkData.getPosition() == null) {
            LOGGER.info("ILinkInfo::getPosition returned null. Link failed.");
        } else if (!linkData.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING.get())
                && world.dimension().location().equals(linkData.getDimension())) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                player.closeContainer();
                player.doCloseContainer();
                player.displayClientMessage(new TranslationTextComponent("message.linkingbooks.no_intraage_linking"),
                        true);
            }
        } else {

            ServerWorld serverWorld = world.getServer()
                    .getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, linkData.getDimension()));

            if (serverWorld == null) {
                LOGGER.info("Cannot find dimension \"" + linkData.getDimension().toString() + "\". Link failed.");
                return false;
            }

            for (LinkEffect effect : linkData.getLinkEffects()) {
                if (!effect.canStartLink(entity, linkData)) {
                    if (entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) entity;
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(new TranslationTextComponent("message.linkingbooks.link_failed_start"),
                                true);
                    }
                    return false;
                }
            }

            for (LinkEffect effect : linkData.getLinkEffects()) {
                effect.onLinkStart(entity, linkData);
            }

            Vector3d originalPos = entity.position();
            float originalRot = entity.yRot;
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

            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                // Deduct experience levels if a cost has been set in config.
                if (!player.isCreative()) {
                    if (player.experienceLevel < ModConfig.COMMON.linkingCostExperienceLevels.get()) {
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(
                                new TranslationTextComponent("message.linkingbooks.insufficient_experience"), true);
                        return false;
                    }
                    player.giveExperienceLevels(ModConfig.COMMON.linkingCostExperienceLevels.get() * -1);
                    tookExperience = true;
                }
                if (holdingBook && linkData.getLinkEffects().contains(LinkEffects.TETHERED.get())) {
                    LinkingBookEntity book = new LinkingBookEntity(world, player.getMainHandItem().copy());
                    Vector3d lookVec = player.getLookAngle();
                    book.setPos(player.getX() + (lookVec.x() / 4.0D), player.getY() + 1.0D,
                            player.getZ() + (lookVec.z() / 4.0D));
                    book.yRot = player.yHeadRot;
                    world.addFreshEntity(book);
                    player.getMainHandItem().shrink(1);
                }
                player.doCloseContainer();
                player.closeContainer();
                player.teleportTo(serverWorld, x, y, z, rotation, player.xRot);
            } else {
                CompoundNBT nbt = new CompoundNBT();
                entity.saveAsPassenger(nbt);
                entity.remove();
                Entity entityCopy = EntityType.create(nbt, serverWorld).orElse(null);
                if (entityCopy == null) {
                    return false;
                }
                entityCopy.setPos(x, y, z);
                serverWorld.addFreshEntity(entityCopy);
                serverWorld.addFromAnotherDimension(entityCopy);
            }
            for (LinkEffect effect : linkData.getLinkEffects()) {
                if (!effect.canFinishLink(entity, linkData)) {
                    if (entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) entity;
                        if (tookExperience) {
                            player.giveExperienceLevels(ModConfig.COMMON.linkingCostExperienceLevels.get());
                        }
                        serverWorld.getServer().execute(() -> {
                            player.teleportTo((ServerWorld) world, originalPos.x, originalPos.y, originalPos.z,
                                    originalRot, player.xRot);
                            player.displayClientMessage(
                                    new TranslationTextComponent("message.linkingbooks.link_failed_end"), true);
                        });
                    } else {
                        serverWorld.getServer().execute(() -> {
                            CompoundNBT tag = new CompoundNBT();
                            entity.saveAsPassenger(tag);
                            entity.remove();
                            Entity entityCopy = EntityType.create(tag, world).orElse(null);
                            if (entityCopy != null) {
                                entityCopy.setPos(originalPos.x, originalPos.y, originalPos.z);
                                world.addFreshEntity(entityCopy);
                                ((ServerWorld) world).addFromAnotherDimension(entityCopy);
                            }
                        });
                    }
                    return false;
                }
            }
            for (LinkEffect effect : linkData.getLinkEffects()) {
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

    public static void openLinkingBookGui(ServerPlayerEntity player, boolean holdingBook, int color, ILinkData linkData,
            ResourceLocation currentDimension) {
        NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> {
            return new LinkingBookContainer(id, playerInventory);
        }, new StringTextComponent("")), extraData -> {
            extraData.writeBoolean(holdingBook);
            extraData.writeInt(color);
            linkData.write(extraData);
            boolean canLink = !currentDimension.equals(linkData.getDimension())
                    || linkData.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING.get());
            extraData.writeBoolean(canLink);
            LinkingBooksSavedData savedData = player.getServer().getLevel(World.OVERWORLD).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData::new, Reference.MOD_ID);
            CompoundNBT image = savedData.getLinkingPanelImage(linkData.getUUID());
            extraData.writeNbt(image);
        });
    }

}
