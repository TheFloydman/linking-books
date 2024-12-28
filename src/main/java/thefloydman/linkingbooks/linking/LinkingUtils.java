/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package thefloydman.linkingbooks.linking;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.FileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thefloydman.linkingbooks.LinkingBooksConfig;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.item.ReltoBookItem;
import thefloydman.linkingbooks.menutype.LinkingBookMenuType;
import thefloydman.linkingbooks.menutype.ReltoBookMenuType;
import thefloydman.linkingbooks.network.client.PlayOwnLinkingSoundMessage;
import thefloydman.linkingbooks.network.client.TakeScreenshotForLinkingBookMessage;
import thefloydman.linkingbooks.world.generation.AgeUtils;
import thefloydman.linkingbooks.world.generation.LinkingBooksDimensionFactory;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class LinkingUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemStack createWrittenLinkingBook(Player player, ItemStack originItem) {

        LinkData linkData = LinkData.fromPlayer(player);
        ItemStack writtenBook = ModItems.WRITTEN_LINKING_BOOK.get().getDefaultInstance();
        writtenBook.set(ModDataComponents.LINK_DATA, linkData);
        writtenBook.set(DataComponents.DYED_COLOR, originItem.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(
                new Color(181, 134, 83).getRGB(), false)));

        PacketDistributor.sendToPlayer((ServerPlayer) player, new TakeScreenshotForLinkingBookMessage(linkData.uuid()));

        return writtenBook;
    }

    /**
     * Teleport an entitiy to a dimension and position using a LinkData.
     * Should only be called server-side.
     *
     * @param entity      the entity to link
     * @param linkData    the LinkData uses to link the entity
     * @param holdingBook whether the entity was holding a book when it linked
     * @return <code>true</code> if the entity successfully teleported; otherwise <code>false</code>
     */
    public static boolean linkEntity(Entity entity, LinkData linkData, boolean holdingBook) {

        Level level = entity.getCommandSenderWorld();

        if (level.isClientSide()) {
            LOGGER.info(
                    "An attempt has been made to directly link an entity from the client. Only do this from the server.");
        } else if (linkData == null) {
            LOGGER.info("A null ILinkInfo has been supplied. Link failed.");
        } else if (!LinkingBooksConfig.ALWAYS_ALLOW_INTRAAGE_LINKING.get()
                && !linkData.linkEffects().contains(ResourceLocation.parse("linkingbooks:intraage_linking"))
                && level.dimension().location().equals(linkData.dimension())) {
            if (entity instanceof ServerPlayer player) {
                player.closeContainer();
                player.doCloseContainer();
                player.displayClientMessage(Component.translatable("message.linkingbooks.no_intraage_linking"), true);
            }
        } else {

            MinecraftServer server = level.getServer();
            if (server == null) {
                LOGGER.info("Cannot get Minecraft server instance. Link failed.");
                return false;
            }

            ServerLevel serverWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, linkData.dimension()));

            if (serverWorld == null) {
                LOGGER.info("Cannot find dimension \"{}\". Link failed.", linkData.dimension());
                return false;
            }

            Set<LinkEffect> linkEffects = linkData.linkEffectsAsLE();

            for (LinkEffect effect : linkEffects) {
                if (!effect.canStartLink().apply(entity, linkData)) {
                    if (entity instanceof ServerPlayer player) {
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(Component.translatable("message.linkingbooks.link_failed_start"),
                                true);
                    }
                    return false;
                }
            }

            for (LinkEffect effect : linkEffects) {
                effect.onLinkStart().accept(entity, linkData);
            }

            Vec3 originalPos = entity.position();
            float originalRot = entity.getYRot();
            BlockPos pos = linkData.blockPos();
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;
            float rotation = linkData.rotation();
            boolean tookExperience = false;

            /*
             * TODO: Find a way to teleport without client moving entity model through
             * world.
             */

            if (entity instanceof ServerPlayer player) {
                // Deduct experience levels if a cost has been set in config.
                if (!player.isCreative()) {
                    if (player.experienceLevel < LinkingBooksConfig.LINKING_COST_LEVELS.get()) {
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(
                                Component.translatable("message.linkingbooks.insufficient_experience"), true);
                        return false;
                    }
                    player.giveExperienceLevels(LinkingBooksConfig.LINKING_COST_LEVELS.get() * -1);
                    tookExperience = true;
                }
                if (holdingBook
                        && !linkData.linkEffects().contains(Reference.getAsResourceLocation("tethered"))) {
                    LinkingBookEntity book = new LinkingBookEntity(level, player.getMainHandItem().copy());
                    Vec3 lookVec = player.getLookAngle();
                    book.setPos(player.getX() + (lookVec.x() / 4.0D), player.getY() + 1.0D,
                            player.getZ() + (lookVec.z() / 4.0D));
                    book.setYRot(player.yHeadRot);
                    level.addFreshEntity(book);
                    player.getMainHandItem().shrink(1);
                }
                player.doCloseContainer();
                player.closeContainer();
                serverWorld.playSound(player, player.blockPosition(), ModSounds.LINK.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                player.teleportTo(serverWorld, x, y, z, rotation, player.getXRot());
                player.resetFallDistance();
                PacketDistributor.sendToPlayer(player, new PlayOwnLinkingSoundMessage());
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
                if (!effect.canFinishLink().apply(entity, linkData)) {
                    if (entity instanceof ServerPlayer player) {
                        if (tookExperience) {
                            player.giveExperienceLevels(LinkingBooksConfig.LINKING_COST_LEVELS.get());
                        }
                        serverWorld.getServer().execute(() -> {
                            player.teleportTo((ServerLevel) level, originalPos.x, originalPos.y, originalPos.z,
                                    originalRot, player.getXRot());
                            player.displayClientMessage(Component.translatable("message.linkingbooks.link_failed_end"),
                                    true);
                        });
                    } else {
                        serverWorld.getServer().execute(() -> {
                            CompoundTag tag = new CompoundTag();
                            entity.saveAsPassenger(tag);
                            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
                            Entity entityCopy = EntityType.create(tag, level).orElse(null);
                            if (entityCopy != null) {
                                entityCopy.setPos(originalPos.x, originalPos.y, originalPos.z);
                                level.addFreshEntity(entityCopy);
                                ((ServerLevel) level).addDuringTeleport(entityCopy);
                            }
                        });
                    }
                    return false;
                }
            }
            for (LinkEffect effect : linkEffects) {
                effect.onLinkEnd().accept(entity, linkData);
            }
            return true;
        }
        return false;
    }

    /**
     * Teleport multiple entities to a dimension and position using the same
     * LinkData. Should only be called server-side.
     *
     * @param entities    a list of entities to link
     * @param linkData    the LinkData uses to link each entity
     * @param holdingBook whether the entities were holding books when they linked
     * @return an <code>int</code> representing the number of entities that were successfully teleported
     */
    public static int linkEntities(List<Entity> entities, LinkData linkData, boolean holdingBook) {
        int linked = 0;
        for (Entity entity : entities) {
            linked += linkEntity(entity, linkData, holdingBook) ? 1 : 0;
        }
        return linked;
    }

    public static int linkToRelto(ServerPlayer player, UUID reltoOwner) {
        ResourceLocation ageResourceLocation = Reference.getAsResourceLocation(String.format("relto_%s", reltoOwner));
        ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, ageResourceLocation);
        Component name = Component.translatable("age.linkingbooks.name.relto");
        Pair<ServerLevel, Boolean> levelPair = AgeUtils.getOrCreateLevel(player.server, levelKey, name, reltoOwner, LinkingBooksDimensionFactory::createRelto);
        if (levelPair.getSecond()) {
            copyRegionFiles(ageResourceLocation);
        }
        LinkData linkData = new LinkData(
                ageResourceLocation,
                new BlockPos(-11, 200, 23),
                -180.0F,
                UUID.randomUUID(),
                List.of(Reference.getAsResourceLocation("intraage_linking"))
        );
        return LinkingUtils.linkEntities(Lists.newArrayList(player), linkData, false);
    }

    public static void openLinkingBookGui(ServerPlayer serverPlayer, boolean holdingBook, int color, LinkData linkData,
                                          ResourceLocation currentDimension) {
        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (id, playerInventory, playerEntity) ->
                                new LinkingBookMenuType(id, playerInventory),
                        Component.literal("")),
                extraData -> {
                    extraData.writeBoolean(holdingBook);
                    extraData.writeInt(color);
                    extraData.writeJsonWithCodec(LinkData.CODEC, linkData);
                    boolean canLink = LinkingBooksConfig.ALWAYS_ALLOW_INTRAAGE_LINKING.get()
                            || !currentDimension.equals(linkData.dimension())
                            || linkData.linkEffects().contains(Reference.getAsResourceLocation("intraage_linking"));
                    extraData.writeBoolean(canLink);
                    MinecraftServer server = serverPlayer.getServer();
                    if (server != null) {
                        ServerLevel overworld = server.overworld();
                        LinkingBooksSavedData savedData = overworld.getDataStorage().computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
                        extraData.writeNbt(savedData.getLinkingPanelImage(linkData.uuid()));
                    }
                });
    }

    public static void openReltoBookGui(ServerPlayer serverPlayer, UUID owner) {
        serverPlayer.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> new ReltoBookMenuType(id, playerInventory), Component.literal("")), extraData -> extraData.writeUUID(owner));
    }

    public static int getLinkingBookColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        if (stack.getItem() instanceof ReltoBookItem) {
            return new Color(77, 196, 109).getRGB();
        }
        DyedItemColor dyedColor = stack.getOrDefault(
                DataComponents.DYED_COLOR,
                new DyedItemColor(new Color(181, 134, 83).getRGB(), false)
        );
        return dyedColor.rgb();
    }

    private static void copyRegionFiles(ResourceLocation ageResourceLocation) {
        Path reltoRegionPath = Reference.server.getWorldPath(new LevelResource("dimensions/linkingbooks/" + ageResourceLocation.getPath() + "/region"));
        try {
            FileUtil.createDirectoriesSafe(reltoRegionPath);
            IModFile modFile = ModList.get().getModFileById(Reference.MODID).getFile();
            Path examples = modFile.findResource("data/linkingbooks/linkingbooks/agetemplate/relto");
            Stream<Path> paths = Files.list(examples);
            List<Path> pathList = paths.filter(fromPath -> fromPath.toString().endsWith(".mca")).toList();
            for (Path fromPath : pathList) {
                Path toPath = reltoRegionPath.resolve(fromPath.getFileName().toString());
                Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LOGGER.error("Could not prefill dimension {}", ageResourceLocation.getPath());
        }
    }
}