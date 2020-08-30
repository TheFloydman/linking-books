package thefloydman.linkingbooks.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.Reference;

public class ModItemGroups {

    public static final ItemGroup LINKING_BOOKS = new ItemGroup(Reference.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.BLACK_WRITTEN_LINKING_BOOK.get());
        }
    };

}
