package thefloydman.linkingbooks.api.capability;

import java.awt.Color;
import java.util.Set;

import net.minecraft.item.ItemStack;

public interface IInkData {

    public void setIngredients(Set<ItemStack> ingredients);

    public Set<ItemStack> getIngredients();

    public float getInkQuality();

    public Color getInkColor();

}
