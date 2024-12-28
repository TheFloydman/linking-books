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

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.Reference;
import thefloydman.linkingbooks.entity.LinkingBookEntity;
import thefloydman.linkingbooks.linking.LinkingUtils;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ReltoBookItem extends Item {

    public ReltoBookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull Component getName(@Nonnull ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("owner")) {
            UUID ownerUUID = customData.copyTag().getUUID("owner");
            String username = Reference.PLAYER_DISPLAY_NAMES.get(ownerUUID);
            if (username != null) {
                return Component.translatable(this.getDescriptionId(itemStack), username);
            }
        }
        return Component.translatable("item.linkingbooks.relto_book_generic");
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!level.isClientSide() && !player.isShiftKeyDown()) {
            CustomData customData = heldStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                Tag ownerTag = customData.copyTag().get("owner");
                if (ownerTag != null && ownerTag.getType() == TagTypes.getType(Tag.TAG_INT_ARRAY)) {
                    LinkingUtils.openReltoBookGui((ServerPlayer) player, customData.copyTag().getUUID("owner"));
                }
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

}
