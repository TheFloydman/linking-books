package thefloydman.linkingbooks.api.capability;

import net.minecraft.item.ItemStack;

public interface ISingleItemStack {

    public void setItemStack(ItemStack stack);

    public ItemStack getItemStack();

}
