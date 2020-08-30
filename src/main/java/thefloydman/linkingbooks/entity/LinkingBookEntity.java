package thefloydman.linkingbooks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;

public class LinkingBookEntity extends ObjectEntity {

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, World world) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F);
    }

    protected LinkingBookEntity(EntityType<? extends LinkingBookEntity> type, World world, ItemStack item) {
        super(type, world, WrittenLinkingBookItem.class, 10.0F, item);
    }

    public LinkingBookEntity(World world) {
        this(ModEntityTypes.LINKING_BOOK.get(), world);
    }

    public LinkingBookEntity(World world, ItemStack item) {
        this(ModEntityTypes.LINKING_BOOK.get(), world, item);
    }

}
