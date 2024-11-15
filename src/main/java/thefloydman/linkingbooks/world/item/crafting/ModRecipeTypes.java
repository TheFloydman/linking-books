package thefloydman.linkingbooks.world.item.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thefloydman.linkingbooks.util.Reference;

import java.util.function.Supplier;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Reference.MODID);

    public static final Supplier<RecipeType<LinkEffectRecipe>> RIGHT_CLICK_BLOCK =
            RECIPE_TYPES.register(
                    Reference.RecipeSerializerNames.LINK_EFFECT,
                    () -> RecipeType.<LinkEffectRecipe>simple(Reference.getAsResourceLocation(Reference.RecipeSerializerNames.LINK_EFFECT))
            );

}