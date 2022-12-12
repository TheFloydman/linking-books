/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.block.LinkTranslatorBlock;
import thefloydman.linkingbooks.block.LinkingLecternBlock;
import thefloydman.linkingbooks.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.blockentity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.blockentity.LinkingBookHolderBlockEntity;
import thefloydman.linkingbooks.blockentity.MarkerSwitchBlockEntity;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.capability.ModCapabilities;
import thefloydman.linkingbooks.command.LinkCommand;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.linking.LinkEffectManager;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.util.Reference;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    /**
     * Called when a player tosses an ItemStack into the world using a key binding
     * (Q by default) or by clicking an item outside an inventory GUI.
     */
    @SubscribeEvent
    public static void tossItem(ItemTossEvent event) {
        ItemStack stack = event.getEntityItem().getItem();
        /*
         * Override WrittenLinkingBookItem.createEntity() specifically for player tosses
         * so that we can use player information to set entity attributes.
         */
        if (stack.getItem() instanceof WrittenLinkingBookItem) {
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
        Level world = event.getWorld();
        Player player = event.getPlayer();
        InteractionHand hand = event.getHand();
        if (world.isClientSide() || hand.equals(InteractionHand.OFF_HAND) || !player.isShiftKeyDown()) {
            return;
        }
        BlockPos pos = event.getPos();
        if (world.getBlockState(pos).getBlock() instanceof LinkingLecternBlock
                || world.getBlockState(pos).getBlock() instanceof LinkTranslatorBlock) {
            BlockEntity generic = world.getBlockEntity(pos);
            if (!(generic instanceof LinkingBookHolderBlockEntity)) {
                return;
            }
            LinkingBookHolderBlockEntity tileEntity = (LinkingBookHolderBlockEntity) generic;
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof WrittenLinkingBookItem && !tileEntity.hasBook()) {
                ILinkData linkData = stack.getCapability(ModCapabilities.LINK_DATA).orElse(new LinkData());
                tileEntity.setBook(stack);
                player.inventoryMenu.broadcastChanges();
                if (world.getBlockEntity(pos) instanceof LinkTranslatorBlockEntity) {
                    LinkTranslatorBlockEntity linkTranslatorTileEntity = (LinkTranslatorBlockEntity) tileEntity;
                    LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(world, pos, linkData, linkTranslatorTileEntity);
                }
            } else if (stack.isEmpty() && tileEntity.hasBook()) {
                player.addItem(tileEntity.getBook());
                player.inventoryMenu.broadcastChanges();
                tileEntity.setBook(ItemStack.EMPTY);
                if (world.getBlockEntity(pos) instanceof LinkTranslatorBlockEntity) {
                    LinkingPortalArea.tryEraseLinkingPortalOnEveryAxis(world, pos);
                }
            }
        } else if (world.getBlockState(pos).getBlock() instanceof MarkerSwitchBlock) {
            BlockState state = world.getBlockState(pos);
            if (state.getValue(MarkerSwitchBlock.OPEN) == true) {
                BlockEntity generic = world.getBlockEntity(pos);
                if (generic instanceof MarkerSwitchBlockEntity) {
                    MarkerSwitchBlockEntity tileEntity = (MarkerSwitchBlockEntity) generic;
                    MarkerSwitchBlockEntity twinEntity = (MarkerSwitchBlockEntity) (state
                            .getValue(MarkerSwitchBlock.HALF) == DoubleBlockHalf.LOWER
                                    ? world.getBlockEntity(pos.above())
                                    : world.getBlockEntity(pos.below()));
                    ItemStack stack = player.getItemInHand(hand);
                    if (!tileEntity.hasItem()) {
                        tileEntity.setItem(stack);
                        twinEntity.setItem(stack);
                        stack.setCount(0);
                        player.inventoryMenu.broadcastChanges();
                    } else {
                        player.addItem(tileEntity.getItem());
                        player.inventoryMenu.broadcastChanges();
                        tileEntity.setItem(ItemStack.EMPTY);
                        twinEntity.setItem(ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Reference.server = event.getServer();
    }

    /**
     * For loading/unloading data.
     */
    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new LinkEffectManager());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LinkCommand.register(event.getDispatcher());
    }

}
