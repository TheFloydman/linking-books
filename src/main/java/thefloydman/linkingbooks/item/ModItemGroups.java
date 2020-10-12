package thefloydman.linkingbooks.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.util.Reference;

public class ModItemGroups {

    public static final ItemGroup LINKING_BOOKS = new ItemGroup(Reference.MOD_ID) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.WRITTEN_LINKING_BOOK.get());
        }

        @Override
        public void fill(NonNullList<ItemStack> items) {
            super.fill(items);
            /*
             * Adds a blank linking book to the Linking Books creative tab for each of the
             * 16 standard dye colors.
             */
            for (DyeColor color : DyeColor.values()) {
                ItemStack stack = ModItems.BLANK_LINKING_BOOK.get().getDefaultInstance();
                IColorCapability cap = stack.getCapability(ColorCapability.COLOR).orElse(null);
                if (cap != null) {
                    cap.setColor(color.getColorValue());
                    items.add(stack);
                }
            }
        }
    };

}
