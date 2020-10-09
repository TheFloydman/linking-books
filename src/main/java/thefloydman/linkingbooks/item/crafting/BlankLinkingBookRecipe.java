package thefloydman.linkingbooks.item.crafting;

import java.awt.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.item.ModItems;

public class BlankLinkingBookRecipe implements ICraftingRecipe {

    private final ResourceLocation id;
    private final ItemStack recipeOutput;
    private final NonNullList<Ingredient> recipeInput;
    private final boolean isSimple;

    public BlankLinkingBookRecipe(ResourceLocation id, ItemStack recipeOutput, NonNullList<Ingredient> recipeInput) {
        this.id = id;
        this.recipeOutput = recipeOutput;
        this.recipeInput = recipeInput;
        this.isSimple = recipeInput.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < inventory.getSizeInventory(); ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (this.isSimple)
                    recipeItemHelper.func_221264_a(itemstack, 1);
                else
                    inputs.add(itemstack);
            }
        }

        return i == this.recipeInput.size() && (this.isSimple ? recipeItemHelper.canCraft(this, (IntList) null)
                : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.recipeInput) != null);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= this.recipeInput.size();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLANK_LINKING_BOOK.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<BlankLinkingBookRecipe> {

        @Override
        public BlankLinkingBookRecipe read(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for blank linking book recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException(
                        "Too many ingredients for blank linking book recipe the max is " + (3 * 3));
            } else {
                float red = 0.0F;
                float green = 0.0F;
                float blue = 0.0F;
                if (json.has("color") && json.get("color").isJsonObject()) {
                    JsonObject color = json.getAsJsonObject("color");
                    if (color.has("red") && color.get("red").isJsonPrimitive()) {
                        red = color.get("red").getAsFloat();
                    }
                    if (color.has("green") && color.get("green").isJsonPrimitive()) {
                        green = color.get("green").getAsFloat();
                    }
                    if (color.has("blue") && color.get("blue").isJsonPrimitive()) {
                        blue = color.get("blue").getAsFloat();
                    }
                }
                ItemStack stack = ModItems.BLANK_LINKING_BOOK.get().getDefaultInstance();
                IColorCapability color = stack.getCapability(ColorCapability.COLOR).orElse(null);
                if (color != null) {
                    color.setColor(new Color(red, green, blue).getRGB());
                }
                return new BlankLinkingBookRecipe(id, stack, nonnulllist);
            }
        }

        @Override
        public BlankLinkingBookRecipe read(ResourceLocation id, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.read(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            return new BlankLinkingBookRecipe(id, itemstack, nonnulllist);
        }

        @Override
        public void write(PacketBuffer buffer, BlankLinkingBookRecipe recipe) {
            buffer.writeVarInt(recipe.recipeInput.size());

            for (Ingredient ingredient : recipe.recipeInput) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.recipeOutput);
        }
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray array) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(array.get(i));
            if (!ingredient.hasNoMatchingItems()) {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }
}
