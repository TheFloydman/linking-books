/*******************************************************************************
 * Linking Books Copyright (C) 2021 TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.entity;

import com.qouteall.immersive_portals.portal.Portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.linking.LinkEffects;

/**
 * Do NOT reference this class without first checking that Immersive Portals is
 * installed.
 */
public class LinkingPortalEntity extends Portal {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(LinkingPortalEntity.class,
            DataSerializers.ITEM_STACK);
    private static final DataParameter<BlockPos> TILEENTITY_POS = EntityDataManager.defineId(LinkingPortalEntity.class,
            DataSerializers.BLOCK_POS);

    public LinkingPortalEntity(EntityType<?> entityType, World world, ItemStack book, BlockPos tileEntityPos) {
        super(entityType, world);
        this.entityData.set(ITEM, book == null ? ItemStack.EMPTY : book);
        this.entityData.set(TILEENTITY_POS, tileEntityPos == null ? BlockPos.ZERO : tileEntityPos);
    }

    public LinkingPortalEntity(EntityType<?> entityType, World world) {
        this(entityType, world, ItemStack.EMPTY, BlockPos.ZERO);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEM, ItemStack.EMPTY);
        this.entityData.define(TILEENTITY_POS, BlockPos.ZERO);
    }

    public BlockPos getTileEntityPos() {
        return this.entityData.get(TILEENTITY_POS);
    }

    public void setTileEntityPos(BlockPos pos) {
        this.entityData.set(TILEENTITY_POS, pos);
    }

    @Override
    public void onEntityTeleportedOnServer(Entity entity) {
        super.onEntityTeleportedOnServer(entity);
        if (!this.entityData.get(ITEM).isEmpty()) {
            ILinkData linkData = this.entityData.get(ITEM).getCapability(LinkData.LINK_DATA).orElse(null);
            for (LinkEffect effect : linkData.getLinkEffects()) {
                effect.onLinkStart(entity, linkData);
                effect.onLinkEnd(entity, linkData);
            }
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!player.isCreative()) {
                player.giveExperienceLevels(ModConfig.COMMON.linkingCostExperienceLevels.get() * -1);
            }
        }
    }

    @Override
    public boolean canTeleportEntity(Entity entity) {
        boolean ip = super.canTeleportEntity(entity);
        boolean lb = true;
        if (!this.entityData.get(ITEM).isEmpty()) {
            ILinkData linkData = this.entityData.get(ITEM).getCapability(LinkData.LINK_DATA).orElse(null);
            if ((this.getDestWorld() == this.getOriginWorld())
                    && !linkData.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING.get())) {
                lb = false;
            } else if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                lb = player.isCreative()
                        || player.experienceLevel >= ModConfig.COMMON.linkingCostExperienceLevels.get();
            }
            for (LinkEffect effect : linkData.getLinkEffects()) {
                if (!effect.canStartLink(entity, linkData) || !effect.canFinishLink(entity, linkData)) {
                    lb = false;
                    break;
                }
            }
        }
        return ip && lb;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("book", NBT.TAG_COMPOUND)) {
            ItemStack book = ItemStack.of(compound.getCompound("book"));
            if (book.getItem() instanceof WrittenLinkingBookItem) {
                this.entityData.set(ITEM, book);
            } else {
                this.entityData.set(ITEM, ItemStack.EMPTY);
            }
        }
        if (compound.contains("tileentity_pos", NBT.TAG_COMPOUND)) {
            this.setTileEntityPos(NBTUtil.readBlockPos(compound.getCompound("tileentity_pos")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        ItemStack item = this.entityData.get(ITEM);
        if (!item.isEmpty()) {
            compound.put("book", item.save(new CompoundNBT()));
        }
        compound.put("tileentity_pos", NBTUtil.writeBlockPos(this.getTileEntityPos()));
    }

}
