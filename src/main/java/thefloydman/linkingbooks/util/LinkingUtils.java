package thefloydman.linkingbooks.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.linking.LinkEffect;
import thefloydman.linkingbooks.network.TakeScreenshotForLinkingBookMessage;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;
import thefloydman.linkingbooks.world.inventory.LinkingBookMenuType;
import thefloydman.linkingbooks.world.item.ModItems;
import net.minecraft.network.chat.Component;
import thefloydman.linkingbooks.ModConfig;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Teleport an entity to a dimension and position.
     */
    public static boolean linkEntity(Entity entity, LinkData linkData, boolean holdingBook) {

        Level world = entity.getCommandSenderWorld();

        if (world.isClientSide()) {
            LOGGER.info(
                    "An attempt has been made to directly link an entity from the client. Only do this from the server.");
        } else if (linkData == null) {
            LOGGER.info("A null ILinkInfo has been supplied. Link failed.");
        } else if (linkData.dimension() == null) {
            LOGGER.info("LinkData.dimension() returned null. Link failed.");
        } else if (linkData.blockPos() == null) {
            LOGGER.info("LinkData.blockPos() returned null. Link failed.");
        } else if (!ModConfig.alwaysAllowIntraAgeLinking
                && !linkData.linkEffects().contains(ResourceLocation.parse("linkingbooks:intraage_linking"))
                && world.dimension().location().equals(linkData.dimension())) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                player.closeContainer();
                player.doCloseContainer();
                player.displayClientMessage(Component.translatable("message.linkingbooks.no_intraage_linking"), true);
            }
        } else {

            ServerLevel serverWorld = world.getServer()
                    .getLevel(ResourceKey.create(Registries.DIMENSION, linkData.dimension()));

            if (serverWorld == null) {
                LOGGER.info("Cannot find dimension \"" + linkData.dimension().toString() + "\". Link failed.");
                return false;
            }

            Set<LinkEffect> linkEffects = linkData.linkEffectsAsLE(serverWorld).stream().filter(Objects::nonNull).collect(Collectors.toSet());

            for (LinkEffect effect : linkEffects) {
                if (!effect.type().canStartLink(entity, linkData)) {
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
                effect.type().onLinkStart(entity, linkData);
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

            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                // Deduct experience levels if a cost has been set in config.
                if (!player.isCreative()) {
                    if (player.experienceLevel < ModConfig.linkingCostLevels) {
                        player.closeContainer();
                        player.doCloseContainer();
                        player.displayClientMessage(
                                Component.translatable("message.linkingbooks.insufficient_experience"), true);
                        return false;
                    }
                    player.giveExperienceLevels(ModConfig.linkingCostLevels * -1);
                    tookExperience = true;
                }
                if (holdingBook
                        && !linkData.linkEffects().contains(Reference.getAsResourceLocation("tethered"))) {
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
                if (!effect.type().canFinishLink(entity, linkData)) {
                    if (entity instanceof ServerPlayer player) {
                        if (tookExperience) {
                            player.giveExperienceLevels(ModConfig.linkingCostLevels);
                        }
                        serverWorld.getServer().execute(() -> {
                            player.teleportTo((ServerLevel) world, originalPos.x, originalPos.y, originalPos.z,
                                    originalRot, player.getXRot());
                            player.displayClientMessage(Component.translatable("message.linkingbooks.link_failed_end"),
                                    true);
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
                effect.type().onLinkEnd(entity, linkData);
            }
            return true;
        }
        return false;
    }

    public static void openLinkingBookGui(ServerPlayer player, boolean holdingBook, int color, LinkData linkData,
                                          ResourceLocation currentDimension) {
        player.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> {
            return new LinkingBookMenuType(id, playerInventory);
        }, Component.literal("")), extraData -> {
            extraData.writeBoolean(holdingBook);
            extraData.writeInt(color);
            extraData.writeJsonWithCodec(LinkData.CODEC, linkData);
            boolean canLink = ModConfig.alwaysAllowIntraAgeLinking
                    || !currentDimension.equals(linkData.dimension())
                    || linkData.linkEffects().contains(Reference.getAsResourceLocation("intraage_linking"));
            extraData.writeBoolean(canLink);
            LinkingBooksSavedData savedData = player.getServer().getLevel(Level.OVERWORLD).getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
            extraData.writeJsonWithCodec(ImageUtils.NATIVE_IMAGE_CODEC, savedData.getLinkingPanelImage(linkData.uuid()));
        });
    }

    public static int getLinkingBookColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        DyedItemColor dyedColor = stack.getOrDefault(
                DataComponents.DYED_COLOR,
                new DyedItemColor(new Color(181, 134, 83).getRGB(), false)
        );
        return dyedColor.rgb();
    }
}