package thefloydman.linkingbooks.world.item;

import java.awt.Color;
import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.world.level.block.ModBlocks;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MODID);

    public static final DeferredItem<Item> GUIDEBOOK = ITEMS.register(Reference.ItemNames.GUIDEBOOK,
            () -> new GuidebookItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> LINKING_PANEL = ITEMS.register(Reference.ItemNames.LINKING_PANEL,
            () -> new LinkingPanelItem(new Item.Properties()));

    public static final DeferredItem<Item> BLANK_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.BLANK_LINKING_BOOK,
            () -> new BlankLinkingBookItem(
                    new Item.Properties()
                            .stacksTo(16)
                            .component(DataComponents.DYED_COLOR, new DyedItemColor(
                                    new Color(181, 134, 83).getRGB(), false))));

    public static final DeferredItem<Item> WRITTEN_LINKING_BOOK = ITEMS.register(
            Reference.ItemNames.WRITTEN_LINKING_BOOK,
            () -> new WrittenLinkingBookItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(DataComponents.DYED_COLOR,
                                    new DyedItemColor(
                                            new Color(181, 134, 83).getRGB(),
                                            false))
                            .component(ModDataComponents.LINK_DATA,
                                    new LinkData(ResourceLocation.parse("minecraft:overworld"),
                                            Reference.server == null ? BlockPos.ZERO : Reference.server.overworld().getSharedSpawnPos(),
                                            0.0F,
                                            UUID.randomUUID(),
                                            new ArrayList<ResourceLocation>()))));

    // Block items

    public static final DeferredItem<Item> BOOKSHELF_STAIRS = ITEMS.register(Reference.BlockNames.BOOKSHELF_STAIRS,
            () -> new BlockItem(ModBlocks.BOOKSHELF_STAIRS.get(), new Item.Properties()));

    public static final DeferredItem<Item> LINKING_LECTERN = ITEMS.register(Reference.BlockNames.LINKING_LECTERN,
            () -> new BlockItem(ModBlocks.LINKING_LECTERN.get(), new Item.Properties()));

    public static final DeferredItem<Item> NARA = ITEMS.register(Reference.BlockNames.NARA,
            () -> new BlockItem(ModBlocks.NARA.get(), new Item.Properties()));

    public static final DeferredItem<Item> LINK_TRANSLATOR = ITEMS.register(Reference.BlockNames.LINK_TRANSLATOR,
            () -> new BlockItem(ModBlocks.LINK_TRANSLATOR.get(), new Item.Properties()));

    public static final DeferredItem<Item> MARKER_SWITCH = ITEMS.register(Reference.BlockNames.MARKER_SWITCH,
            () -> new BlockItem(ModBlocks.MARKER_SWITCH.get(), new Item.Properties()));

}