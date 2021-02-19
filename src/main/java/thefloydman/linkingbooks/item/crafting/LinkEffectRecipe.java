/*******************************************************************************
 * Linking Books - Fabric
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;

public class LinkEffectRecipe implements ICraftingRecipe {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> recipeInputs;
    private Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();

    public LinkEffectRecipe(ResourceLocation id, NonNullList<Ingredient> recipeInput, Set<LinkEffect> linkEffects) {
        this.linkEffects = linkEffects;
        this.recipeInputs = recipeInput;
        this.id = id;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<LinkEffectRecipe> {

        @Override
        public LinkEffectRecipe read(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> ingredients = readIngredients(
                    JSONUtils.getJsonArray(json, "additional_ingredients"));
            if (ingredients.size() > 8) {
                throw new JsonParseException(
                        "Too many additional ingredients for written linking book recipe. The max is 8");
            } else {
                Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();
                JsonArray jsonArray = JSONUtils.getJsonArray(json, "link_effects");
                for (JsonElement element : jsonArray) {
                    linkEffects.add(LinkEffect.get(new ResourceLocation(element.getAsString())));
                }
                return new LinkEffectRecipe(id, ingredients, linkEffects);
            }
        }

        @Override
        public LinkEffectRecipe read(ResourceLocation id, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.read(buffer));
            }

            Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();
            int quantity = buffer.readInt();
            for (int j = 0; j < quantity; j++) {
                linkEffects.add(LinkEffect.get(new ResourceLocation(buffer.readString())));
            }

            return new LinkEffectRecipe(id, ingredients, linkEffects);
        }

        @Override
        public void write(PacketBuffer buffer, LinkEffectRecipe recipe) {
            buffer.writeVarInt(recipe.recipeInputs.size());

            for (Ingredient ingredient : recipe.recipeInputs) {
                ingredient.write(buffer);
            }

            buffer.writeInt(recipe.linkEffects.size());
            for (LinkEffect effect : recipe.linkEffects) {
                buffer.writeString(effect.getRegistryName().toString());
            }
        }
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray array) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(array.get(i));
            if (!ingredient.hasNoMatchingItems()) {
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;
        // Determines how many non-empty stacks are in the crafting grid. Also places
        // non-empty stacks into a DefaultedList.
        NonNullList<Ingredient> craftingInputs = NonNullList.create();
        for (int j = 0; j < inventory.getSizeInventory(); ++j) {
            ItemStack stack = inventory.getStackInSlot(j);
            if (!stack.isEmpty()) {
                ++i;
                inputs.add(stack);
                craftingInputs.add(Ingredient.fromStacks(inventory.getStackInSlot(j)));
            }
        }

        // Checks if the previously created DefaultedList exactly matches the inputs for
        // this recipe.
        boolean matches = true;
        for (int j = 0; j < craftingInputs.size(); j++) {
            boolean foundMatch = false;
            for (int k = 0; k < this.recipeInputs.size(); k++) {
                ItemStack[] stacks = this.recipeInputs.get(k).getMatchingStacks();
                for (ItemStack stack : stacks) {
                    if (stack.getItem() == craftingInputs.get(j).getMatchingStacks()[0].getItem()
                            || craftingInputs.get(j).getMatchingStacks()[0]
                                    .getItem() instanceof WrittenLinkingBookItem) {
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

        return i == this.recipeInputs.size() + 1 && matches;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack writtenBook = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).getItem() instanceof WrittenLinkingBookItem) {
                writtenBook = inv.getStackInSlot(i).copy();
                break;
            }
        }
        if (!writtenBook.isEmpty()) {
            ILinkData linkData = writtenBook.getCapability(LinkData.LINK_DATA).orElse(null);
            if (linkData != null) {
                for (LinkEffect effect : this.linkEffects) {
                    if (!linkData.addLinkEffect(effect)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return writtenBook;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= this.recipeInputs.size();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ModItems.WRITTEN_LINKING_BOOK.get().getDefaultInstance();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLANK_LINKING_BOOK.get();
    }
}
