package thefloydman.linkingbooks.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkingBookCapabilityProvider;

public abstract class LinkingBookItem extends Item {

    public LinkingBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new LinkingBookCapabilityProvider();
    }

    /**
     * Used to color item texture. Any tintIndex besides 0 will return -1.
     */
    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
        if (color != null) {
            return color.getColor();
        }
        return DyeColor.GREEN.getColorValue();
    }

    /*
     * These two methods help ensure that itemstacks with capabilities in the
     * creative menu and crafting table result keep their capabilities when placed
     * in the player's inventories.
     * 
     * TODO: Remove when this issue is fixed:
     * https://github.com/brandon3055/Draconic-Evolution/blob/
     * 4af607da1f7eb144cd6fed5708611a86356f5c66/src/main/java/com/brandon3055/
     * draconicevolution/items/equipment/IModularItem.java#L219-L227
     */

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
        if (color != null) {
            CompoundNBT tag = color.writeToShareTag(nbt);
            return tag;
        }
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        stack.setTag(nbt);
        if (nbt != null) {
            IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
            if (color != null) {
                color.readFromShareTag(nbt);
            }
        }
    }

}
