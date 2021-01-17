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
package thefloydman.linkingbooks.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.block.LinkingLecternBlock;
import thefloydman.linkingbooks.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.command.LinkCommand;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.tileentity.LinkingLecternTileEntity;
import thefloydman.linkingbooks.tileentity.MarkerSwitchTileEntity;
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
            PlayerEntity player = event.getPlayer();
            World world = event.getEntity().getEntityWorld();
            LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
            Vector3d lookVec = player.getLookVec();
            entity.setPosition(player.getPosX() + lookVec.getX(), player.getPosY() + 1.75D + lookVec.getY(),
                    player.getPosZ() + lookVec.getZ());
            entity.rotationYaw = player.rotationYawHead;
            entity.addVelocity(lookVec.x / 4, lookVec.y / 4, lookVec.z / 4);
            world.addEntity(entity);
        }
    }

    /**
     * Use to attach capabilities to items not native to Linking Books.
     */
    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {

    }

    /**
     * Use to attach capabilities to entities not native to Linking Books.
     */
    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {

    }

    /**
     * Use to attach capabilities to tileentities not native to Linking Books.
     */
    @SubscribeEvent
    public static void attachTileEntityCapabilities(AttachCapabilitiesEvent<TileEntity> event) {

    }

    @SubscribeEvent
    public static void serverStarting(FMLServerStartingEvent event) {
        // Register commands.
        LinkCommand.register(event.getServer().getCommandManager().getDispatcher());
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();
        if (world.isRemote() || hand.equals(Hand.OFF_HAND) || !player.isSneaking()) {
            return;
        }
        BlockPos pos = event.getPos();
        if (world.getBlockState(pos).getBlock() instanceof LinkingLecternBlock) {
            TileEntity generic = world.getTileEntity(pos);
            if (!(generic instanceof LinkingLecternTileEntity)) {
                return;
            }
            LinkingLecternTileEntity tileEntity = (LinkingLecternTileEntity) generic;
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getItem() instanceof WrittenLinkingBookItem && !tileEntity.hasBook()) {
                tileEntity.setBook(stack);
                IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
                player.container.detectAndSendChanges();
            } else if (stack.isEmpty() && tileEntity.hasBook()) {
                player.addItemStackToInventory(tileEntity.getBook());
                player.container.detectAndSendChanges();
                tileEntity.setBook(ItemStack.EMPTY);
            }
        } else if (world.getBlockState(pos).getBlock() instanceof MarkerSwitchBlock) {
            BlockState state = world.getBlockState(pos);
            if (state.get(MarkerSwitchBlock.OPEN) == true) {
                TileEntity generic = world.getTileEntity(pos);
                if (generic instanceof MarkerSwitchTileEntity) {
                    MarkerSwitchTileEntity tileEntity = (MarkerSwitchTileEntity) generic;
                    MarkerSwitchTileEntity twinEntity = (MarkerSwitchTileEntity) (state
                            .get(MarkerSwitchBlock.HALF) == DoubleBlockHalf.LOWER ? world.getTileEntity(pos.up())
                                    : world.getTileEntity(pos.down()));
                    ItemStack stack = player.getHeldItem(hand);
                    if (tileEntity.isEmpty()) {
                        tileEntity.setItem(stack);
                        twinEntity.setItem(stack);
                        stack.setCount(0);
                        player.container.detectAndSendChanges();
                    } else if (tileEntity.hasItem()) {
                        player.addItemStackToInventory(tileEntity.getItem());
                        player.container.detectAndSendChanges();
                        tileEntity.setItem(ItemStack.EMPTY);
                        twinEntity.setItem(ItemStack.EMPTY);
                    }
                }
            }
        }
    }

}
