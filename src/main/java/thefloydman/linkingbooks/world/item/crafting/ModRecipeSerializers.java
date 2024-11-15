package thefloydman.linkingbooks.world.item.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

import java.util.function.Supplier;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(BuiltInRegistries.RECIPE_SERIALIZER, Reference.MODID);

    public static final Supplier<LinkEffectRecipe.Serializer> LINK_EFFECT = RECIPE_SERIALIZERS
            .register(Reference.RecipeSerializerNames.LINK_EFFECT, LinkEffectRecipe.Serializer::new);

}