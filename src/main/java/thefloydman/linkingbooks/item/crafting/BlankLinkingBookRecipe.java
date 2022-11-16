/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.item.crafting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
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
    private final NonNullList<Ingredient> recipeInputs;

    public BlankLinkingBookRecipe(ResourceLocation id, ItemStack recipeOutput, NonNullList<Ingredient> recipeInput) {
        this.id = id;
        this.recipeOutput = recipeOutput;
        this.recipeInputs = recipeInput;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;
        // Determines how many non-empty stacks are in the crafting grid. Also places
        // non-empty stacks into a DefaultedList.
        NonNullList<Ingredient> craftingInputs = NonNullList.create();
        for (int j = 0; j < inventory.getContainerSize(); ++j) {
            ItemStack stack = inventory.getItem(j);
            if (!stack.isEmpty()) {
                ++i;
                inputs.add(stack);
                craftingInputs.add(Ingredient.of(inventory.getItem(j)));
            }
        }

        // Checks if the previously created DefaultedList exactly matches the inputs for
        // this recipe.
        boolean matches = true;
        for (int j = 0; j < craftingInputs.size(); j++) {
            boolean foundMatch = false;
            for (int k = 0; k < this.recipeInputs.size(); k++) {
                ItemStack[] stacks = this.recipeInputs.get(k).getItems();
                for (ItemStack stack : stacks) {
                    if (stack.getItem() == craftingInputs.get(j).getItems()[0].getItem()) {
                        foundMatch = true;
                        break;
                    }
                }
            }
            if (!foundMatch) {
                matches = false;
                break;
            }
        }

        return i == this.recipeInputs.size() && matches;
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.recipeInputs.size();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLANK_LINKING_BOOK.get();
    }

    @Override
    public ItemStack getResultItem() {
        return this.recipeOutput;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeInputs;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<BlankLinkingBookRecipe> {

        @Override
        public BlankLinkingBookRecipe fromJson(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getAsJsonArray(json, "ingredients"));
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
        public BlankLinkingBookRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack itemstack = buffer.readItem();
            return new BlankLinkingBookRecipe(id, itemstack, nonnulllist);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, BlankLinkingBookRecipe recipe) {
            buffer.writeVarInt(recipe.recipeInputs.size());

            for (Ingredient ingredient : recipe.recipeInputs) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.recipeOutput);
        }
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray array) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(array.get(i));
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }
}
