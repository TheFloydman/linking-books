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
package thefloydman.linkingbooks.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ModCapabilities;
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
            ILinkData linkData = heldStack.getCapability(ModCapabilities.LINK_DATA).orElse(null);
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
        ILinkData linkData = stack.getCapability(ModCapabilities.LINK_DATA).orElse(null);
        if (linkData != null) {
            tooltip.add(new TextComponent("§eAge: §9§o" + linkData.getDimension().toString()));
            tooltip.add(new TextComponent("§ePosition: §9§o(" + linkData.getPosition().getX() + ", "
                    + linkData.getPosition().getY() + ", " + linkData.getPosition().getZ() + ")"));
            Set<ResourceLocation> linkEffects = new HashSet<ResourceLocation>(linkData.getLinkEffectsAsRL());
            if (!linkEffects.isEmpty()) {
                tooltip.add(new TextComponent("§eLink Effects:"));
                for (ResourceLocation effect : linkEffects) {
                    tooltip.add(new TextComponent("    §9§o"
                            + new TranslatableComponent("linkEffect." + effect.getNamespace() + "." + effect.getPath())
                                    .getString()));
                }
            }
        }
    }

}
