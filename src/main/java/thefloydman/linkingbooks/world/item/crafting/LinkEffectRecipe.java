package thefloydman.linkingbooks.world.item.crafting;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import thefloydman.linkingbooks.core.component.ModDataComponents;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.linking.LinkEffect;
import thefloydman.linkingbooks.world.item.ModItems;
import thefloydman.linkingbooks.world.item.WrittenLinkingBookItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LinkEffectRecipe implements CraftingRecipe {

    private final NonNullList<Ingredient> recipeInputs;
    private final NonNullList<ResourceLocation> linkEffects;
    private ItemStack outputStack = ItemStack.EMPTY;

    public LinkEffectRecipe(NonNullList<Ingredient> recipeInput,
                            NonNullList<ResourceLocation> linkEffects) {
        this.linkEffects = linkEffects;
        this.recipeInputs = recipeInput;
        int pos = recipeInput.indexOf(Ingredient.of(ModItems.WRITTEN_LINKING_BOOK));
        if (pos > -1) {
            this.outputStack = recipeInput.get(pos).getItems()[0];
        }
    }

    public static class Serializer implements RecipeSerializer<LinkEffectRecipe> {

        private static final MapCodec<LinkEffectRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instanceBuilder -> instanceBuilder.group(
                                Ingredient.CODEC_NONEMPTY
                                        .listOf()
                                        .fieldOf("additional_ingredients")
                                        .flatXmap(
                                                ingredientsList -> {
                                                    Ingredient[] aingredient = ingredientsList.toArray(Ingredient[]::new); // Neo skip the empty check and immediately create the array.
                                                    if (aingredient.length == 0) {
                                                        return DataResult.error(() -> "No ingredients to add link effects");
                                                    } else {
                                                        return aingredient.length > (ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()) - 1
                                                                ? DataResult.error(() -> "Too many ingredients for link effect recipe. The maximum is: %s".formatted((ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()) - 1))
                                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(linkEffectRecipe -> linkEffectRecipe.recipeInputs),
                                ResourceLocation.CODEC
                                        .listOf()
                                        .fieldOf("link_effects")
                                        .flatXmap(
                                                effectsList -> {
                                                    ResourceLocation[] linkEffects = effectsList.toArray(ResourceLocation[]::new);
                                                    if (linkEffects.length == 0) {
                                                        return DataResult.error(() -> "No link effects for link effect recipe");
                                                    } else {
                                                        return DataResult.success(NonNullList.of(ResourceLocation.parse("linkingbooks:none"), linkEffects));
                                                    }
                                                },
                                                DataResult::success)
                                        .forGetter(linkEffectRecipe -> linkEffectRecipe.linkEffects)
                        )
                        .apply(instanceBuilder, LinkEffectRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, LinkEffectRecipe> STREAM_CODEC = StreamCodec.of(
                LinkEffectRecipe.Serializer::toNetwork, LinkEffectRecipe.Serializer::fromNetwork
        );

        @Override
        public @Nonnull MapCodec<LinkEffectRecipe> codec() {
            return CODEC;
        }

        @Override
        public @Nonnull StreamCodec<RegistryFriendlyByteBuf, LinkEffectRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static LinkEffectRecipe fromNetwork(RegistryFriendlyByteBuf byteBuf) {
            int ingredientsSize = ByteBufCodecs.VAR_INT.decode(byteBuf);
            NonNullList<Ingredient> ingredientsList = NonNullList.withSize(ingredientsSize, Ingredient.EMPTY);
            ingredientsList.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(byteBuf));
            int effectsSize = ByteBufCodecs.VAR_INT.decode(byteBuf);
            NonNullList<ResourceLocation> effectsList = NonNullList.withSize(effectsSize, ResourceLocation.parse("linkingbooks:none"));
            effectsList.replaceAll(resourceLocation -> ResourceLocation.STREAM_CODEC.decode(byteBuf));
            return new LinkEffectRecipe(ingredientsList, effectsList);
        }

        private static void toNetwork(RegistryFriendlyByteBuf byteBuf, LinkEffectRecipe linkEffectRecipe) {
            ByteBufCodecs.VAR_INT.encode(byteBuf, linkEffectRecipe.recipeInputs.size());
            for (Ingredient recipeInput : linkEffectRecipe.recipeInputs) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(byteBuf, recipeInput);
            }
            ByteBufCodecs.VAR_INT.encode(byteBuf, linkEffectRecipe.linkEffects.size());
            for (ResourceLocation linkEffect : linkEffectRecipe.linkEffects) {
                ResourceLocation.STREAM_CODEC.encode(byteBuf, linkEffect);
            }
        }

    }

    @Override
    public boolean matches(CraftingInput inventory, @Nonnull Level level) {
        int i = 0;
        // Determines how many non-empty stacks are in the crafting grid. Also places
        // non-empty stacks into a DefaultedList.
        NonNullList<Ingredient> craftingInputs = NonNullList.create();
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack stack = inventory.getItem(j);
            if (!stack.isEmpty()) {
                ++i;
                craftingInputs.add(Ingredient.of(inventory.getItem(j)));
            }
        }

        // Checks if the previously created DefaultedList exactly matches the inputs for
        // this recipe.
        boolean matches = true;
        for (Ingredient craftingInput : craftingInputs) {
            boolean foundMatch = false;
            for (Ingredient recipeInput : this.recipeInputs) {
                ItemStack[] stacks = recipeInput.getItems();
                for (ItemStack stack : stacks) {
                    if (stack.getItem() == craftingInput.getItems()[0].getItem()
                            || craftingInput.getItems()[0].getItem() instanceof WrittenLinkingBookItem) {
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
    public @Nonnull ItemStack assemble(CraftingInput inv, @Nonnull HolderLookup.Provider registryAccess) {
        ItemStack writtenBook = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getItem(i).getItem() instanceof WrittenLinkingBookItem) {
                writtenBook = inv.getItem(i).copy();
                break;
            }
        }
        if (!writtenBook.isEmpty()) {
            LinkData originalLinkData = writtenBook.get(ModDataComponents.LINK_DATA);
            if (originalLinkData != null) {
                List<ResourceLocation> allLinkEffects = new ArrayList<>();
                allLinkEffects.addAll(originalLinkData.linkEffects());
                allLinkEffects.addAll(this.linkEffects);
                LinkData updatedLinkData = new LinkData(originalLinkData.dimension(), originalLinkData.blockPos(), originalLinkData.rotation(), originalLinkData.uuid(), allLinkEffects);
                writtenBook.set(ModDataComponents.LINK_DATA, updatedLinkData);
            }
        }
        return writtenBook;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.recipeInputs.size();
    }

    @Override
    public @Nonnull ItemStack getResultItem(@Nonnull HolderLookup.Provider pRegistries) {
        return this.outputStack;
    }

    @Override
    public @Nonnull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.LINK_EFFECT.get();
    }

    @Override
    public @Nonnull CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }
}