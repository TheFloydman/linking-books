package thefloydman.linkingbooks.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;
import thefloydman.linkingbooks.world.entity.LinkingBookEntity;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WrittenLinkingBookItem extends Item {

    public WrittenLinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            LinkData linkData = heldStack.get(ModDataComponents.LINK_DATA);
            if (linkData != null) {
                LinkingUtils.openLinkingBookGui((ServerPlayer) player, true, LinkingUtils.getLinkingBookColor(heldStack, 0),
                        linkData, world.dimension().location());
            }
        }
        return InteractionResultHolder.pass(heldStack);
    }

    @Override
    public Entity createEntity(@Nonnull Level world, Entity itemEntity, ItemStack stack) {
        LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
        entity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
        entity.setYRot(itemEntity.getYRot());
        entity.setDeltaMovement(itemEntity.getDeltaMovement());
        return entity;
    }

    @Override
    public boolean hasCustomEntity(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext tooltipContext, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, tooltipContext, tooltip, flag);
        LinkData linkData = stack.get(ModDataComponents.LINK_DATA);
        if (linkData != null) {
            tooltip.add(Component.literal("§eAge: §9§o" + linkData.dimension().toString()));
            tooltip.add(Component.literal("§ePosition: §9§o(" + linkData.blockPos().getX() + ", "
                    + linkData.blockPos().getY() + ", " + linkData.blockPos().getZ() + ")"));
            Set<ResourceLocation> linkEffects = new HashSet<>(linkData.linkEffects());
            if (!linkEffects.isEmpty()) {
                tooltip.add(Component.literal("§eLink Effects:"));
                for (ResourceLocation effect : linkEffects) {
                    tooltip.add(Component.literal("    §9§o" + Component
                            .translatable("linkEffect." + effect.getNamespace() + "." + effect.getPath()).getString()));
                }
            }
        }
    }

}