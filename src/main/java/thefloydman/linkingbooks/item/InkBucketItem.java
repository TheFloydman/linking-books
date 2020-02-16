package thefloydman.linkingbooks.item;

import java.util.function.Supplier;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;

public class InkBucketItem extends BucketItem {

    public InkBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder);
    }

}
