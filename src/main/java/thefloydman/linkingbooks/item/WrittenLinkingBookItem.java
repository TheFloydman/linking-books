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
package thefloydman.linkingbooks.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.Capabilities;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.util.LinkingUtils;

public class WrittenLinkingBookItem extends LinkingBookItem {

    public WrittenLinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            ILinkData linkData = heldStack.getCapability(Capabilities.LINK_DATA).orElse(null);
            if (linkData != null) {
                LinkingUtils.openLinkingBookGui((ServerPlayer) player, true, LinkingBookItem.getColor(heldStack, 0),
                        linkData, world.dimension().location());
            }
        }
        return InteractionResultHolder.pass(heldStack);
    }

    @Override
    public Entity createEntity(Level world, Entity itemEntity, ItemStack stack) {
        LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
        entity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
        entity.setYRot(itemEntity.getYRot());
        entity.setDeltaMovement(itemEntity.getDeltaMovement());
        return entity;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        ILinkData linkData = stack.getCapability(Capabilities.LINK_DATA).orElse(null);
        if (linkData != null) {
            tooltip.add(Component.literal("§eAge: §9§o" + linkData.getDimension().toString()));
            tooltip.add(Component.literal("§ePosition: §9§o(" + linkData.getPosition().getX() + ", "
                    + linkData.getPosition().getY() + ", " + linkData.getPosition().getZ() + ")"));
            Set<LinkEffect> linkEffects = new HashSet<LinkEffect>(linkData.getLinkEffects());
            if (!linkEffects.isEmpty()) {
                tooltip.add(Component.literal("§eLink Effects:"));
                for (LinkEffect effect : linkEffects) {
                    tooltip.add(
                            Component
                                    .literal("    §9§o" + Component
                                            .translatable(
                                                    "linkEffect." + LinkEffect.registry.getKey(effect).getNamespace()
                                                            + "." + LinkEffect.registry.getKey(effect).getPath())
                                            .getString()));
                }
            }
        }
    }

}
