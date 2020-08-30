package thefloydman.linkingbooks.api.capability;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IInkData {

    public float getInkQuality();

    public void setInkQuality(float quality);

    public List<ItemStack> getIngredients();

    public void setIngredients(List<ItemStack> ingredients);

}
