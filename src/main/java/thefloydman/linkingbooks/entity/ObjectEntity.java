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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;

public class ObjectEntity extends Entity {

    private static final DataParameter<Float> DURABILITY = EntityDataManager.defineId(ObjectEntity.class,
            DataSerializers.FLOAT);
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(ObjectEntity.class,
            DataSerializers.ITEM_STACK);

    private static final String LABEL_DURABILITY = "Durability";
    private static final String LABEL_ITEM = "Item";
    private static final String LABEL_HURTTIME = "HurtTime";

    private final Class<? extends Item> itemClass;
    private final float maxDurability;
    public int hurtTime;

    public ObjectEntity(EntityType<? extends ObjectEntity> type, World world, Class<? extends Item> itemClass,
            float maxDurability) {
        super(type, world);
        this.itemClass = itemClass;
        this.maxDurability = maxDurability;
        this.setDurability(this.getMaxDurability());
        this.hurtTime = 0;
    }

    public ObjectEntity(EntityType<? extends ObjectEntity> type, World world, Class<? extends Item> itemClass,
            float maxDurability, ItemStack item) {
        this(type, world, itemClass, maxDurability);
        this.setItem(item);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DURABILITY, 1.0F);
        this.entityData.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains(LABEL_DURABILITY, NBT.TAG_ANY_NUMERIC)) {
            this.setDurability(compound.getFloat(LABEL_DURABILITY));
        }
        if (compound.contains(LABEL_ITEM, NBT.TAG_COMPOUND)) {
            ItemStack stack = ItemStack.of(compound.getCompound(LABEL_ITEM));
            if (itemClass.isInstance(stack.getItem())) {
                this.setItem(stack);
            } else {
                this.setItem(ItemStack.EMPTY);
            }
        }

        this.hurtTime = compound.getShort(LABEL_HURTTIME);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.putFloat(LABEL_DURABILITY, this.getDurability());
        compound.put(LABEL_ITEM, this.getItem().serializeNBT());
        compound.putShort(LABEL_HURTTIME, (short) this.hurtTime);
    }

    /**
     * Makes sure the entity spawns correctly client-side.
     */
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public final float getMaxDurability() {
        return this.maxDurability;
    }

    public float getDurability() {
        return this.entityData.get(DURABILITY);
    }

    public void setDurability(float health) {
        this.entityData.set(DURABILITY, MathHelper.clamp(health, 0.0F, this.getMaxDurability()));
    }

    public ItemStack getItem() {
        return this.entityData.get(ITEM);
    }

    public void setItem(ItemStack item) {
        this.entityData.set(ITEM, item);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.hurtTime = 10;
        this.setDurability(this.getDurability() - amount);
        return super.hurt(source, amount);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.getItem().isEmpty() || this.getDurability() <= 0.0F) {
            this.onKilled();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        Vector3d vec3d = this.getDeltaMovement();
        double d1 = vec3d.x;
        double d3 = vec3d.y;
        double d5 = vec3d.z;
        if (this.onGround) {
            d1 *= 0.8D;
            d5 *= 0.8D;
        }
        if (Math.abs(vec3d.x) < 0.003D) {
            d1 = 0.0D;
        }
        if (Math.abs(vec3d.y) < 0.003D) {
            d3 = 0.0D;
        }
        if (Math.abs(vec3d.z) < 0.003D) {
            d5 = 0.0D;
        }
        this.setDeltaMovement(d1, d3, d5);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public void onKilled() {
        this.kill();
    }

    @Override
    public void playerTouch(PlayerEntity player) {
        if (this.distanceTo(player) < 0.75) {
            player.push(this);
        }
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public boolean isPushable() {
        return this.isAlive();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize();
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 = d0 * 256.0D * getViewScale();
        return distance < d0 * d0;
    }

}
