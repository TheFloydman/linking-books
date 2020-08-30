package thefloydman.linkingbooks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;

public class DescriptiveBookEntity extends ObjectEntity {

    protected DescriptiveBookEntity(EntityType<? extends DescriptiveBookEntity> type, World world) {
        super(type, world, WrittenLinkingBookItem.class, 20.0F);
    }

    protected DescriptiveBookEntity(EntityType<? extends DescriptiveBookEntity> type, World world, ItemStack item) {
        super(type, world, WrittenLinkingBookItem.class, 20.0F, item);
    }

    public DescriptiveBookEntity(World world) {
        this(ModEntityTypes.DESCRIPTIVE_BOOK.get(), world);
    }

    public DescriptiveBookEntity(World world, ItemStack item) {
        this(ModEntityTypes.DESCRIPTIVE_BOOK.get(), world, item);
    }

}