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

package thefloydman.linkingbooks.item;

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
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.linking.LinkingUtils;

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
            tooltip.add(Component.translatable("§eAge: §9§o%s", linkData.dimensionName()));
            tooltip.add(Component.literal("§eCoordinates: §9§o(" + linkData.blockPos().getX() + ", "
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