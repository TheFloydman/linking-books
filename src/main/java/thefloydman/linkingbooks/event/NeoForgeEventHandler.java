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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingPortalArea;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;
import thefloydman.linkingbooks.world.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.world.level.block.LinkTranslatorBlock;
import thefloydman.linkingbooks.world.level.block.LinkingLecternBlock;
import thefloydman.linkingbooks.world.level.block.MarkerSwitchBlock;
import thefloydman.linkingbooks.world.level.block.entity.LinkTranslatorBlockEntity;
import thefloydman.linkingbooks.world.level.block.entity.LinkingBookHolderBlockEntity;
import thefloydman.linkingbooks.world.level.block.entity.MarkerSwitchBlockEntity;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeEventHandler {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Reference.server = event.getServer();
    }

    @SubscribeEvent
    public static void tossItem(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
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
        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (level.isClientSide() || hand.equals(InteractionHand.OFF_HAND) || !player.isShiftKeyDown()) {
            return;
        }
        BlockPos blockPos = event.getPos();
        if (level.getBlockState(blockPos).getBlock() instanceof LinkingLecternBlock
                || level.getBlockState(blockPos).getBlock() instanceof LinkTranslatorBlock) {
            BlockEntity generic = level.getBlockEntity(blockPos);
            if (!(generic instanceof LinkingBookHolderBlockEntity linkingBookHolderBlockEntity)) {
                return;
            }
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof WrittenLinkingBookItem && !linkingBookHolderBlockEntity.hasBook()) {
                LinkData linkData = itemStack.getOrDefault(ModDataComponents.LINK_DATA, LinkData.EMPTY);
                ItemStack returnedItemStack = linkingBookHolderBlockEntity.insertItem(0, itemStack, false);
                player.getInventory().setItem(player.getInventory().findSlotMatchingItem(itemStack), returnedItemStack);
                player.inventoryMenu.broadcastChanges();
                if (level.getBlockEntity(blockPos) instanceof LinkTranslatorBlockEntity) {
                    LinkTranslatorBlockEntity linkTranslatorTileEntity = (LinkTranslatorBlockEntity) linkingBookHolderBlockEntity;
                    LinkingPortalArea.tryMakeLinkingPortalOnEveryAxis(level, blockPos, linkData, linkTranslatorTileEntity);
                }
            } else if (itemStack.isEmpty() && linkingBookHolderBlockEntity.hasBook()) {
                ItemStack extractedStack = linkingBookHolderBlockEntity.extractItem(0, Integer.MAX_VALUE, false);
                player.addItem(extractedStack);
                player.inventoryMenu.broadcastChanges();
                if (level.getBlockEntity(blockPos) instanceof LinkTranslatorBlockEntity) {
                    LinkingPortalArea.tryEraseLinkingPortalOnEveryAxis(level, blockPos);
                }
            }
        } else if (level.getBlockState(blockPos).getBlock() instanceof MarkerSwitchBlock) {
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
                        ItemStack returnedItemStack = originalBlockEntity.insertItem(0, itemStack, false);
                        if (genericTwinBlockEntity instanceof MarkerSwitchBlockEntity twinBlockEntity) {
                            twinBlockEntity.insertItem(0, itemStack, false);
                        }
                        player.getInventory().setItem(player.getInventory().findSlotMatchingItem(itemStack), returnedItemStack);
                        player.inventoryMenu.broadcastChanges();
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

}