package thefloydman.linkingbooks.item;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import thefloydman.linkingbooks.fluid.LinkingBooksFluids;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.ItemNames;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class LinkingBooksItems {

    @ObjectHolder(ItemNames.PLAIN_INK_BUCKET)
    public static final Item PLAIN_INK_BUCKET = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry()
                .registerAll(new BucketItem(() -> LinkingBooksFluids.PLAIN_INK,
                        new Item.Properties().group(ItemGroup.MISC).maxStackSize(1).containerItem(Items.BUCKET))
                                .setRegistryName(Reference.MOD_ID, Reference.ItemNames.PLAIN_INK_BUCKET));
    }

}
