package thefloydman.linkingbooks.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.command.LinkCommand;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
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
        if (stack.getItem() instanceof WrittenLinkingBookItem) {
            event.setCanceled(true);
            PlayerEntity player = event.getPlayer();
            World world = event.getEntity().getEntityWorld();
            LinkingBookEntity entity = new LinkingBookEntity(world, stack);
            Vector3d lookVec = player.getLookVec();
            entity.setPosition(player.getPosX() + lookVec.getX(), player.getPosY() + 1.75D + lookVec.getY(),
                    player.getPosZ() + lookVec.getZ());
            entity.rotationYaw = player.rotationYawHead;
            entity.addVelocity(lookVec.x / 4, lookVec.y / 4, lookVec.z / 4);
            world.addEntity(entity);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (target instanceof LinkingBookEntity && !event.getPlayer().getEntityWorld().isRemote()) {
            PlayerEntity player = event.getPlayer();
            LinkingBookEntity bookEntity = (LinkingBookEntity) target;
            if (event.getHand().equals(Hand.MAIN_HAND)) {
                ItemStack bookStack = bookEntity.getItem();
                if (!bookStack.isEmpty()) {
                    if (player.isSneaking()) {
                        player.addItemStackToInventory(bookStack);

                        // Syncs player inventory to client:
                        ((ServerPlayerEntity) (player)).sendContainerToPlayer(player.container);

                        target.remove();
                    } else {
                        ILinkData linkCapability = bookStack.getCapability(LinkData.LINK_DATA).orElse(null);
                        if (linkCapability != null) {
                            /**
                             * TODO: Open linking book GUI.
                             */
                        }
                    }
                }

            }
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

    @SubscribeEvent
    public static void serverStarting(FMLServerStartingEvent event) {
        LinkCommand.register(event.getServer().getCommandManager().getDispatcher());
    }

}
