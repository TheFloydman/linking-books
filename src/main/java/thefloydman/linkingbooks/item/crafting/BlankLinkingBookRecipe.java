package thefloydman.linkingbooks.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.item.ModItems;

public class BlankLinkingBookRecipe implements ICraftingRecipe {

    private final ResourceLocation id;
    private final int color;

    public BlankLinkingBookRecipe(ResourceLocation id, int color) {
        this.id = id;
        this.color = color;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return this.canFit(inv.getWidth(), inv.getHeight());
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = ModItems.BLANK_LINKING_BOOK.get().getDefaultInstance();
        IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
        if (color != null) {
            color.setColor(this.color);
        }
        return stack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLANK_LINKING_BOOK.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

}
