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

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.api.linking.LinkEffect;
import thefloydman.linkingbooks.capability.Capabilities;
import thefloydman.linkingbooks.item.ModItems;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;

public class LinkEffectRecipe implements CraftingRecipe {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> recipeInputs;
    private Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();
    private ItemStack outputStack = ItemStack.EMPTY;

    public LinkEffectRecipe(ResourceLocation id, NonNullList<Ingredient> recipeInput, Set<LinkEffect> linkEffects) {
        this.linkEffects = linkEffects;
        this.recipeInputs = recipeInput;
        this.id = id;
        int pos = -1;
        List<Item> writtenBooks = Lists.asList(ModItems.BLACK_WRITTEN_LINKING_BOOK.get(),
                new Item[] { ModItems.BLUE_WRITTEN_LINKING_BOOK.get(), ModItems.BROWN_WRITTEN_LINKING_BOOK.get(),
                        ModItems.CYAN_WRITTEN_LINKING_BOOK.get(), ModItems.GRAY_WRITTEN_LINKING_BOOK.get(),
                        ModItems.GREEN_WRITTEN_LINKING_BOOK.get(), ModItems.LIGHT_BLUE_WRITTEN_LINKING_BOOK.get(),
                        ModItems.LIGHT_GRAY_WRITTEN_LINKING_BOOK.get(), ModItems.LIME_WRITTEN_LINKING_BOOK.get(),
                        ModItems.MAGENTA_WRITTEN_LINKING_BOOK.get(), ModItems.ORANGE_WRITTEN_LINKING_BOOK.get(),
                        ModItems.PINK_WRITTEN_LINKING_BOOK.get(), ModItems.PURPLE_WRITTEN_LINKING_BOOK.get(),
                        ModItems.RED_WRITTEN_LINKING_BOOK.get(), ModItems.WHITE_WRITTEN_LINKING_BOOK.get(),
                        ModItems.YELLOW_WRITTEN_LINKING_BOOK.get() });
        for (int i = 0; i < writtenBooks.size() && pos == -1; i++) {
            pos = recipeInput.indexOf(Ingredient.of(writtenBooks.get(i)));
        }
        if (pos > -1) {
            outputStack = recipeInput.get(pos).getItems()[0];
        }
    }

    public static class Serializer implements RecipeSerializer<LinkEffectRecipe> {

        @Override
        public LinkEffectRecipe fromJson(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> ingredients = readIngredients(
                    GsonHelper.getAsJsonArray(json, "additional_ingredients"));
            if (ingredients.size() > 8) {
                throw new JsonParseException(
                        "Too many additional ingredients for written linking book recipe. The max is 8");
            } else {
                Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();
                JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "link_effects");
                for (JsonElement element : jsonArray) {
                    linkEffects.add(LinkEffect.get(new ResourceLocation(element.getAsString())));
                }
                return new LinkEffectRecipe(id, ingredients, linkEffects);
            }
        }

        @Override
        public LinkEffectRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(buffer));
            }

            Set<LinkEffect> linkEffects = new HashSet<LinkEffect>();
            int quantity = buffer.readInt();
            for (int j = 0; j < quantity; j++) {
                linkEffects.add(LinkEffect.get(new ResourceLocation(buffer.readUtf())));
            }

            return new LinkEffectRecipe(id, ingredients, linkEffects);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LinkEffectRecipe recipe) {
            buffer.writeVarInt(recipe.recipeInputs.size());

            for (Ingredient ingredient : recipe.recipeInputs) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeInt(recipe.linkEffects.size());
            for (LinkEffect effect : recipe.linkEffects) {
                buffer.writeUtf(LinkEffect.registry.getKey(effect).toString());
            }
        }
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray array) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(array.get(i));
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
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
                    if (stack.getItem() == craftingInputs.get(j).getItems()[0].getItem()
                            || craftingInputs.get(j).getItems()[0].getItem() instanceof WrittenLinkingBookItem) {
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
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack writtenBook = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() instanceof WrittenLinkingBookItem) {
                writtenBook = inv.getItem(i).copy();
                break;
            }
        }
        if (!writtenBook.isEmpty()) {
            ILinkData linkData = writtenBook.getCapability(Capabilities.LINK_DATA).orElse(null);
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
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.recipeInputs.size();
    }

    @Override
    public ItemStack getResultItem() {
        return this.outputStack;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.LINK_EFFECT.get();
    }
}