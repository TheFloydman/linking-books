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
package thefloydman.linkingbooks.entity;

import com.qouteall.immersive_portals.portal.Portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.config.ModConfig;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.linking.LinkEffects;

public class LinkingPortalEntity extends Portal {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(LinkingPortalEntity.class,
            DataSerializers.ITEMSTACK);

    public LinkingPortalEntity(EntityType<?> entityType, World world, ItemStack book) {
        super(entityType, world);
        this.dataManager.set(ITEM, book == null ? ItemStack.EMPTY : book);
    }

    public LinkingPortalEntity(EntityType<?> entityType, World world) {
        this(entityType, world, ItemStack.EMPTY);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void onEntityTeleportedOnServer(Entity entity) {
        super.onEntityTeleportedOnServer(entity);
        if (!this.dataManager.get(ITEM).isEmpty()) {
            ILinkData linkData = this.dataManager.get(ITEM).getCapability(LinkData.LINK_DATA).orElse(null);
            for (LinkEffect effect : linkData.getLinkEffects()) {
                effect.onLinkStart(entity, linkData);
                effect.onLinkEnd(entity, linkData);
            }
        }
    }

    @Override
    public boolean canTeleportEntity(Entity entity) {
        boolean ip = super.canTeleportEntity(entity);
        boolean lb = true;
        if (!this.dataManager.get(ITEM).isEmpty()) {
            ILinkData linkData = this.dataManager.get(ITEM).getCapability(LinkData.LINK_DATA).orElse(null);
            if ((this.getDestWorld() == this.getOriginWorld())
                    && !linkData.getLinkEffects().contains(LinkEffects.INTRAAGE_LINKING)) {
                lb = false;
            }
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                player.giveExperiencePoints(ModConfig.COMMON.linkingCostExperiencePoints.get() * -1);
                player.addExperienceLevel(ModConfig.COMMON.linkingCostExperienceLevels.get() * -1);
                if (player.experienceLevel < 0 && !player.isCreative()) {
                    player.addExperienceLevel(ModConfig.COMMON.linkingCostExperienceLevels.get());
                    player.giveExperiencePoints(ModConfig.COMMON.linkingCostExperiencePoints.get());
                    lb = false;
                }
            }
        }
        return ip && lb;
    }

    @Override
    protected void func_213281_b(CompoundNBT compound) {
        super.func_213281_b(compound);
        if (compound.contains("book", NBT.TAG_COMPOUND)) {
            ItemStack book = ItemStack.read(compound.getCompound("book"));
            if (book.getItem() instanceof WrittenLinkingBookItem) {
                this.dataManager.set(ITEM, book);
            } else {
                this.dataManager.set(ITEM, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void func_70037_a(CompoundNBT compound) {
        super.func_70037_a(compound);
        ItemStack item = this.dataManager.get(ITEM);
        if (!item.isEmpty()) {
            compound.put("book", item.write(new CompoundNBT()));
        }
    }

}
