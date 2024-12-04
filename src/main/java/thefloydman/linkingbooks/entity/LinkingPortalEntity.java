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

package thefloydman.linkingbooks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.portal.Portal;
import thefloydman.linkingbooks.LinkingBooksConfig;
import thefloydman.linkingbooks.component.ModDataComponents;
import thefloydman.linkingbooks.component.LinkData;
import thefloydman.linkingbooks.linking.LinkEffect;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;

import javax.annotation.Nonnull;

/**
 * Do NOT reference this class without first checking that Immersive Portals is
 * installed.
 */
public class LinkingPortalEntity extends Portal {

    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(LinkingPortalEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<BlockPos> BLOCKENTITY_POS = SynchedEntityData.defineId(LinkingPortalEntity.class,
            EntityDataSerializers.BLOCK_POS);

    public LinkingPortalEntity(EntityType<?> entityType, Level world, ItemStack book, BlockPos tileEntityPos) {
        super(entityType, world);
        this.entityData.set(ITEM, book == null ? ItemStack.EMPTY : book);
        this.entityData.set(BLOCKENTITY_POS, tileEntityPos == null ? BlockPos.ZERO : tileEntityPos);
    }

    public LinkingPortalEntity(EntityType<?> entityType, Level world) {
        this(entityType, world, ItemStack.EMPTY, BlockPos.ZERO);
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder pBuilder) {
        pBuilder.define(ITEM, ItemStack.EMPTY);
        pBuilder.define(BLOCKENTITY_POS, BlockPos.ZERO);
    }

    public BlockPos getTileEntityPos() {
        return this.entityData.get(BLOCKENTITY_POS);
    }

    public void setTileEntityPos(BlockPos pos) {
        this.entityData.set(BLOCKENTITY_POS, pos);
    }

    @Override
    public void onEntityTeleportedOnServer(Entity entity) {
        super.onEntityTeleportedOnServer(entity);
        if (!this.entityData.get(ITEM).isEmpty()) {
            LinkData linkData = this.entityData.get(ITEM).get(ModDataComponents.LINK_DATA);
            for (LinkEffect effect : linkData.linkEffectsAsLE()) {
                effect.onLinkStart().accept(entity, linkData);
                effect.onLinkEnd().accept(entity, linkData);
            }
        }
        if (entity instanceof Player player) {
            if (!player.isCreative()) {
                player.giveExperienceLevels(LinkingBooksConfig.LINKING_COST_LEVELS.get() * -1);
            }
        }
    }

    @Override
    public boolean canTeleportEntity(Entity entity) {
        boolean ip = super.canTeleportEntity(entity);
        boolean lb = true;
        if (!this.entityData.get(ITEM).isEmpty()) {
            LinkData linkData = this.entityData.get(ITEM).get(ModDataComponents.LINK_DATA);
            if ((this.getDestWorld() == this.getOriginWorld())
                    && !linkData.linkEffectsAsLE().contains(LinkingBooksConfig.ALWAYS_ALLOW_INTRAAGE_LINKING.get())) {
                lb = false;
            } else if (entity instanceof Player player) {
                lb = player.isCreative()
                        || player.experienceLevel >= LinkingBooksConfig.LINKING_COST_LEVELS.get();
            }
            for (LinkEffect effect : linkData.linkEffectsAsLE()) {
                if (!effect.canStartLink().apply(entity, linkData) || !effect.canFinishLink().apply(entity, linkData)) {
                    lb = false;
                    break;
                }
            }
        }
        return ip && lb;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("book", Tag.TAG_COMPOUND)) {
            ItemStack book = ItemStack.parseOptional(this.registryAccess(), compound.getCompound("book"));
            if (book.getItem() instanceof WrittenLinkingBookItem) {
                this.entityData.set(ITEM, book);
            } else {
                this.entityData.set(ITEM, ItemStack.EMPTY);
            }
        }
        if (compound.contains("blockentity_pos", Tag.TAG_COMPOUND)) {
            this.setTileEntityPos(NbtUtils.readBlockPos(compound, "blockentity_pos").get());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ItemStack item = this.entityData.get(ITEM);
        if (!item.isEmpty()) {
            compound.put("book", item.save(this.registryAccess()));
        }
        compound.put("blockentity_pos", NbtUtils.writeBlockPos(this.getTileEntityPos()));
    }

}