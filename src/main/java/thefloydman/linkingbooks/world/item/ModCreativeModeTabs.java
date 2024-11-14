package thefloydman.linkingbooks.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MODID);

    private static final int[] BOOK_COLORS = {
            -16777216, // black
            -15704088, // blue
            -10275065, // brown
            -10564885, // cyan
            -9211021, // gray
            -16741881, // green
            -5843457, // light blue
            -4342339, // light gray
            -8329083, // lime
            -3197338, // magenta
            -1203700, // orange
            -546604, // pink
            -7989784, // purple
            -3076337, // red
            -328966, // white
            -623 // yellow
    };

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.linkingbooks")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ModItems.BOOKSHELF_STAIRS.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(ModItems.GUIDEBOOK.get());
        output.accept(ModItems.LINKING_LECTERN.get());
        output.accept(ModItems.NARA.get());
        output.accept(ModItems.LINK_TRANSLATOR.get());
        output.accept(ModItems.MARKER_SWITCH.get());
        output.accept(ModItems.LINKING_PANEL.get());
        output.accept(ModItems.BOOKSHELF_STAIRS.get());
        output.accept(ModItems.BLANK_LINKING_BOOK.get());
        for (int color : BOOK_COLORS) {
            ItemStack linkingBook = ModItems.BLANK_LINKING_BOOK.get().getDefaultInstance();
            linkingBook.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
            output.accept(linkingBook);
        }
    }).build());

}