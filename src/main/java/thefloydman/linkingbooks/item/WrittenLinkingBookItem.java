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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.util.LinkingUtils;

public class WrittenLinkingBookItem extends LinkingBookItem {

    public WrittenLinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (!world.isRemote() && !player.isSneaking()) {
            ILinkData linkData = heldStack.getCapability(LinkData.LINK_DATA).orElse(null);
            IColorCapability color = heldStack.getCapability(ColorCapability.COLOR).orElse(null);
            if (linkData != null && color != null) {
                LinkingUtils.openLinkingBookGui((ServerPlayerEntity) player, true, color.getColor(), linkData,
                        world.getDimensionKey().getLocation());
            }
        }
        return ActionResult.resultPass(heldStack);
    }

    @Override
    public Entity createEntity(World world, Entity itemEntity, ItemStack stack) {
        LinkingBookEntity entity = new LinkingBookEntity(world, stack.copy());
        entity.setPosition(itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ());
        entity.rotationYaw = itemEntity.rotationYaw;
        entity.setMotion(itemEntity.getMotion());
        return entity;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        ILinkData linkData = stack.getCapability(LinkData.LINK_DATA).orElse(null);
        if (linkData != null) {
            tooltip.add(new StringTextComponent("§eAge: §9§o" + linkData.getDimension().toString()));
            tooltip.add(new StringTextComponent("§ePosition: §9§o(" + linkData.getPosition().getX() + ", "
                    + linkData.getPosition().getY() + ", " + linkData.getPosition().getZ() + ")"));
            Set<LinkEffect> linkEffects = new HashSet<LinkEffect>(linkData.getLinkEffects());
            if (!linkEffects.isEmpty()) {
                tooltip.add(new StringTextComponent("§eLink Effects:"));
                for (LinkEffect effect : linkEffects) {
                    tooltip.add(new StringTextComponent("    §9§o" + effect.getRegistryName().toString()));
                }
            }
        }
    }

}
