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

package thefloydman.linkingbooks.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import thefloydman.linkingbooks.LinkingBooksConfig;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.block.LinkTranslatorBlock;
import thefloydman.linkingbooks.block.LinkingLecternBlock;
import thefloydman.linkingbooks.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.blockentity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.blockentity.LinkingBookHolderBlockEntity;
import thefloydman.linkingbooks.blockentity.LinkingLecternBlockEntity;
import thefloydman.linkingbooks.blockentity.MarkerSwitchBlockEntity;
import thefloydman.linkingbooks.commands.LinkCommand;
import thefloydman.linkingbooks.commands.ReltoCommand;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.item.ReltoBookItem;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.linking.LinkingPortalArea;
import thefloydman.linkingbooks.linking.LinkingUtils;
import thefloydman.linkingbooks.network.client.UpdatePlayerDisplayNames;
import thefloydman.linkingbooks.world.generation.AgeUtils;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameEventHandler {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Reference.server = event.getServer();
        // Add existing Ages to level map so the game knows they exist!
        AgeUtils.mapLevelsOnStartup(event.getServer());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LinkCommand.register(event.getDispatcher());
        ReltoCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void tossItem(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (stack.getItem() instanceof WrittenLinkingBookItem || stack.getItem() instanceof ReltoBookItem) {
            event.setCanceled(true);
            Player player = event.getPlayer();
            Level world = event.getEntity().getCommandSenderWorld();
            LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
            Vec3 lookVec = player.getLookAngle();
            entity.setPos(player.getX() + lookVec.x(), player.getY() + 1.75D + lookVec.y(),
                    player.getZ() + lookVec.z());
            entity.setYRot(player.yHeadRot);
            entity.push(lookVec.x / 4, lookVec.y / 4, lookVec.z / 4);
            world.addFreshEntity(entity);
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (level.isClientSide() || hand.equals(InteractionHand.OFF_HAND) || !player.isShiftKeyDown()) {
            return;
        }
        BlockPos blockPos = event.getPos();
        Block block = level.getBlockState(blockPos).getBlock();
        if (block instanceof LinkingLecternBlock || block instanceof LinkTranslatorBlock) {
            BlockEntity generic = level.getBlockEntity(blockPos);
            if (!(generic instanceof LinkingBookHolderBlockEntity linkingBookHolderBlockEntity)) {
                return;
            }
            ItemStack itemStack = player.getItemInHand(hand);
            if ((itemStack.getItem() instanceof WrittenLinkingBookItem || itemStack.getItem() instanceof ReltoBookItem) && !linkingBookHolderBlockEntity.hasBook()) {
                ItemStack returnedItemStack = linkingBookHolderBlockEntity.insertItem(0, itemStack, false);
                player.getInventory().setItem(player.getInventory().findSlotMatchingItem(itemStack), returnedItemStack);
                player.inventoryMenu.broadcastChanges();
                if (level.getBlockEntity(blockPos) instanceof LinkTranslatorBlockEntity) {
                    LinkTranslatorBlockEntity linkTranslatorTileEntity = (LinkTranslatorBlockEntity) linkingBookHolderBlockEntity;
                    LinkData linkData = itemStack.getOrDefault(ModDataComponents.LINK_DATA, LinkData.EMPTY);
                    LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(level, blockPos, linkData, linkTranslatorTileEntity);
                }
            } else if (itemStack.isEmpty() && linkingBookHolderBlockEntity.hasBook()) {
                ItemStack extractedStack = linkingBookHolderBlockEntity.extractItem(0, Integer.MAX_VALUE, false);
                player.addItem(extractedStack);
                player.inventoryMenu.broadcastChanges();
                if (level.getBlockEntity(blockPos) instanceof LinkTranslatorBlockEntity linkTranslatorBlockEntity) {
                    if (Reference.isImmersivePortalsLoaded()) {
                        ImmersivePortalsIntegration.deleteLinkingPortals(linkTranslatorBlockEntity);
                    }
                    LinkingPortalArea.tryEraseLinkingPortalOnEveryAxis(level, blockPos);
                }
            }
        } else if (block instanceof MarkerSwitchBlock) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getValue(MarkerSwitchBlock.OPEN)) {
                BlockEntity genericOriginalBlockEntity = level.getBlockEntity(blockPos);
                if (genericOriginalBlockEntity instanceof MarkerSwitchBlockEntity originalBlockEntity) {
                    BlockEntity genericTwinBlockEntity = blockState
                            .getValue(MarkerSwitchBlock.HALF) == DoubleBlockHalf.LOWER
                            ? level.getBlockEntity(blockPos.above())
                            : level.getBlockEntity(blockPos.below());
                    ItemStack itemStack = player.getItemInHand(hand);
                    if (!originalBlockEntity.hasItem()) {
                        if (!itemStack.isEmpty()) {
                            ItemStack returnedItemStack = originalBlockEntity.insertItem(0, itemStack, false);
                            if (genericTwinBlockEntity instanceof MarkerSwitchBlockEntity twinBlockEntity) {
                                twinBlockEntity.insertItem(0, itemStack, false);
                            }
                            player.getInventory().setItem(player.getInventory().selected, returnedItemStack);
                            player.inventoryMenu.broadcastChanges();
                        }
                    } else {
                        ItemStack extractedStack = originalBlockEntity.extractItem(0, Integer.MAX_VALUE, false);
                        player.addItem(extractedStack);
                        player.inventoryMenu.broadcastChanges();
                        if (genericTwinBlockEntity instanceof MarkerSwitchBlockEntity twinBlockEntity) {
                            twinBlockEntity.extractItem(0, Integer.MAX_VALUE, false);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            Level level = player.level();

            // Give guidebook.
            final String guidebookGivenTag = Reference.MODID + ":guidebook_given";
            final boolean givingGuidebookOkay = LinkingBooksConfig.GIVE_GUIDEBOOK_ON_FIRST_JOIN.get();
            if (!level.isClientSide() && !player.getTags().contains(guidebookGivenTag) && givingGuidebookOkay) {
                ItemHandlerHelper.giveItemToPlayer(player, ModItems.GUIDEBOOK.toStack());
                player.addTag(guidebookGivenTag);
            }

            // Give Relto book.
            final String reltoBookGivenTag = Reference.MODID + ":relto_book_given";
            final boolean givingReltoBookOkay = LinkingBooksConfig.GIVE_RELTO_BOOK_ON_FIRST_JOIN.get();
            final ItemStack reltoBookStack = ModItems.RELTO_BOOK.toStack();
            final CompoundTag ownerTag = new CompoundTag();
            ownerTag.putUUID("owner", player.getUUID());
            reltoBookStack.set(DataComponents.CUSTOM_DATA, CustomData.of(ownerTag));
            if (!level.isClientSide() && !player.getTags().contains(reltoBookGivenTag) && givingReltoBookOkay) {
                ItemHandlerHelper.giveItemToPlayer(player, reltoBookStack);
                player.addTag(reltoBookGivenTag);
            }

            // Set overworld book in Relto.
            ResourceLocation levelKey = level.dimension().location();
            if (levelKey.getNamespace().equals(Reference.MODID) && levelKey.getPath().equals(String.format("relto_%s", player.getUUID()))) {
                final String reltoOverworldBookSet = Reference.MODID + ":relto_overworld_book_set";
                if (!level.isClientSide() && !player.getTags().contains(reltoOverworldBookSet)) {
                    BlockPos lecternPos = new BlockPos(-11, 201, 6);
                    BlockEntity genericBlockEntity = level.getBlockEntity(lecternPos);
                    if (genericBlockEntity instanceof LinkingLecternBlockEntity linkingLecternBlockEntity) {
                        LinkData linkData = new LinkData(Reference.server.overworld().dimension().location(), Reference.server.overworld().getSharedSpawnPos(), Reference.server.overworld().getSharedSpawnAngle(), UUID.randomUUID(), List.of());
                        ItemStack bookItemStack = ModItems.WRITTEN_LINKING_BOOK.toStack();
                        bookItemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(new Color(77, 196, 109).getRGB(), false));
                        bookItemStack.set(ModDataComponents.LINK_DATA, linkData);
                        linkingLecternBlockEntity.insertItem(0, bookItemStack, false);
                        player.addTag(reltoOverworldBookSet);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (!level.isClientSide()) {
            ResourceLocation levelKey = level.dimension().location();
            if (levelKey.getNamespace().equals(Reference.MODID) && levelKey.getPath().startsWith("relto")) {
                if (entity.getY() < level.getMinBuildHeight()) {
                    LinkData linkData = new LinkData(levelKey, new BlockPos(-11, 200, 23), -180.0F, UUID.randomUUID(), List.of(Reference.getAsResourceLocation("intraage_linking")));
                    LinkingUtils.linkEntity(entity, linkData, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!level.isClientSide()) {
            // Update SavedData
            LinkingBooksSavedData linkingBooksSavedData = player.getServer().overworld().getDataStorage()
                    .computeIfAbsent(LinkingBooksSavedData.factory(), Reference.MODID);
            linkingBooksSavedData.addPlayerDisplayName(player.getUUID(), player.getDisplayName().getString());

            // Updated server-side map
            Reference.PLAYER_DISPLAY_NAMES.putAll(linkingBooksSavedData.playerDisplayNames);

            // Update client-side map
            PacketDistributor.sendToAllPlayers(new UpdatePlayerDisplayNames(linkingBooksSavedData.playerDisplayNames));
        }
    }

    @SubscribeEvent
    public static void onPlayerCraftItem(PlayerEvent.ItemCraftedEvent event) {
        ItemStack itemStack = event.getCrafting();
        if (itemStack.getItem() instanceof ReltoBookItem) {
            CompoundTag ownerTag = new CompoundTag();
            ownerTag.putUUID("owner", event.getEntity().getUUID());
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(ownerTag));
        }
    }

}