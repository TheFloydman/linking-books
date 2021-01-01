package thefloydman.linkingbooks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.util.LinkingUtils;

public class LinkingBookEntity extends ObjectEntity {

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, World world) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F);
        setRenderDistanceWeight(2.0D);
    }

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, World world, ItemStack item) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F, item);
        setRenderDistanceWeight(2.0D);
    }

    public LinkingBookEntity(World world) {
        this(ModEntityTypes.LINKING_BOOK.get(), world);
    }

    public LinkingBookEntity(World world, ItemStack item) {
        this(ModEntityTypes.LINKING_BOOK.get(), world, item);
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (!player.getEntityWorld().isRemote()) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            if (hand == Hand.MAIN_HAND) {
                ItemStack bookStack = this.getItem();
                if (!bookStack.isEmpty()) {
                    if (serverPlayer.isSneaking()) {
                        serverPlayer.addItemStackToInventory(bookStack);
                        serverPlayer.container.detectAndSendChanges();
                        this.remove();
                        return ActionResultType.SUCCESS;
                    } else {
                        ILinkData linkData = bookStack.getCapability(LinkData.LINK_DATA).orElse(null);
                        IColorCapability color = bookStack.getCapability(ColorCapability.COLOR).orElse(null);
                        if (linkData != null && color != null) {
                            LinkingUtils.openLinkingBookGui(serverPlayer, false, color.getColor(), linkData,
                                    serverPlayer.getEntityWorld().getDimensionKey().getLocation());
                            return ActionResultType.CONSUME;
                        }
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

}
