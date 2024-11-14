package thefloydman.linkingbooks.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.world.inventory.GuidebookMenuType;

import javax.annotation.Nonnull;
import java.util.List;

public class GuidebookItem extends Item {

    public GuidebookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && player instanceof ServerPlayer) {
            player.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> {
                return new GuidebookMenuType(id, playerInventory);
            }, Component.literal("")));
        }
        return InteractionResultHolder.pass(heldStack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext tooltipContext, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, tooltipContext, tooltip, flag);
        tooltip.add(
                Component.literal("§9§o" + Component.translatable("item.linkingbooks.guidebook.subtitle").getString()));
    }

}