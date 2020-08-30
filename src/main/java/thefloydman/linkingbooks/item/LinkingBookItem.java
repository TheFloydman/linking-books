package thefloydman.linkingbooks.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;

public abstract class LinkingBookItem extends Item {

    protected final DyeColor color;

    public LinkingBookItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }

}
