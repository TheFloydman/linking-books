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

}
