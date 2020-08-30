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
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;
import thefloydman.linkingbooks.LinkingBooks;

public class ObjectEntity extends Entity {

    private static final DataParameter<Float> DURABILITY = EntityDataManager.createKey(ObjectEntity.class,
            DataSerializers.FLOAT);
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ObjectEntity.class,
            DataSerializers.ITEMSTACK);

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
    protected void registerData() {
        this.dataManager.register(DURABILITY, 1.0F);
        this.dataManager.register(ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains(LABEL_DURABILITY, NBT.TAG_ANY_NUMERIC)) {
            this.setDurability(compound.getFloat(LABEL_DURABILITY));
        }
        if (compound.contains(LABEL_ITEM, NBT.TAG_COMPOUND)) {
            ItemStack stack = ItemStack.read(compound.getCompound(LABEL_ITEM));
            if (itemClass.isInstance(stack.getItem())) {
                this.setItem(stack);
            } else {
                this.setItem(ItemStack.EMPTY);
            }
        }

        this.hurtTime = compound.getShort(LABEL_HURTTIME);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putFloat(LABEL_DURABILITY, this.getDurability());
        compound.put(LABEL_ITEM, this.getItem().serializeNBT());
        compound.putShort(LABEL_HURTTIME, (short) this.hurtTime);
    }

    /**
     * Makes sure the entity spawns correctly client-side.
     */
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public final float getMaxDurability() {
        return this.maxDurability;
    }

    public float getDurability() {
        return this.dataManager.get(DURABILITY);
    }

    public void setDurability(float health) {
        this.dataManager.set(DURABILITY, MathHelper.clamp(health, 0.0F, this.getMaxDurability()));
    }

    public ItemStack getItem() {
        return this.dataManager.get(ITEM);
    }

    public void setItem(ItemStack item) {
        this.dataManager.set(ITEM, item);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        this.hurtTime = 10;
        this.setDurability(this.getDurability() - amount);
        return super.attackEntityFrom(source, amount);
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
        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }
        Vector3d vec3d = this.getMotion();
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
        this.setMotion(d1, d3, d5);
        this.move(MoverType.SELF, this.getMotion());
    }

    public void onKilled() {
        LinkingBooks.LOGGER.info("KILLED");
        this.onKillCommand();
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity player) {
        if (MathHelper
                .sqrt(Math.pow(player.getPosX() - this.getPosX(), 2) + Math.pow(player.getPosY() - this.getPosY(), 2)
                        + Math.pow(player.getPosZ() - this.getPosZ(), 2)) < 0.75) {
            player.applyEntityCollision(this);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean canBePushed() {
        return this.isAlive();
    }

}
