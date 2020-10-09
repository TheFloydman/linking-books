package thefloydman.linkingbooks.item.crafting;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.util.Reference;

public class ModRecipeSerializers {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

    public static final RegistryObject<BlankLinkingBookRecipeSerializer<BlankLinkingBookRecipe>> BLANK_LINKING_BOOK = RECIPES
            .register(Reference.RecipeSerializerNames.BLANK_LINKING_BOOK,
                    () -> new BlankLinkingBookRecipeSerializer<>((id, color) -> new BlankLinkingBookRecipe(id, color)));

}
