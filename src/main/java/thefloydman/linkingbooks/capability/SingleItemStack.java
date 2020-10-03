package thefloydman.linkingbooks.capability;

import javax.annotation.Nullable;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import thefloydman.linkingbooks.api.capability.ISingleItemStack;

public class SingleItemStack {

    @CapabilityInject(ISingleItemStack.class)
    public static final Capability<ISingleItemStack> ITEMSTACK = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISingleItemStack.class, new Storage(), Default::new);
    }

    public static class Default implements ISingleItemStack {

        private ItemStack itemStack = ItemStack.EMPTY;

        @Override
        public void setItemStack(ItemStack stack) {
            this.itemStack = stack;
        }

        @Override
        public ItemStack getItemStack() {
            return this.itemStack;
        }

    }

    public static class Storage implements Capability.IStorage<ISingleItemStack> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<ISingleItemStack> capability, ISingleItemStack instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("itemstack",
                    ItemStackHelper.saveAllItems(new CompoundNBT(), NonNullList.from(instance.getItemStack())));
            return nbt;
        }

        @Override
        public void readNBT(Capability<ISingleItemStack> capability, ISingleItemStack instance, Direction side,
                INBT nbt) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT compound = (CompoundNBT) nbt;
                if (compound.contains("itemstack", NBT.TAG_COMPOUND)) {
                    NonNullList<ItemStack> list = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(compound.getCompound("itemstack"), list);
                    instance.setItemStack(list.get(0));
                }
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        private LazyOptional<ISingleItemStack> instance = LazyOptional.of(() -> ITEMSTACK.getDefaultInstance());

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap.equals(ITEMSTACK) && ITEMSTACK != null) {
                return instance.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return ITEMSTACK.getStorage().writeNBT(ITEMSTACK, instance.orElse(ITEMSTACK.getDefaultInstance()), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            ITEMSTACK.getStorage().readNBT(ITEMSTACK, instance.orElse(ITEMSTACK.getDefaultInstance()), null, nbt);
        }

    }

}
