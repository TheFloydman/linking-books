package thefloydman.linkingbooks.item.crafting;

import java.awt.Color;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class BlankLinkingBookRecipeSerializer<T extends IRecipe<?>>
        extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
    private final BiFunction<ResourceLocation, Integer, T> recipeFactory;

    public BlankLinkingBookRecipeSerializer(BiFunction<ResourceLocation, Integer, T> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public T read(ResourceLocation id, JsonObject json) {
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
        return this.recipeFactory.apply(id, new Color(red, green, blue).getRGB());
    }

    @Override
    public T read(ResourceLocation id, PacketBuffer buffer) {
        return this.recipeFactory.apply(id, 0);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
    }
}
