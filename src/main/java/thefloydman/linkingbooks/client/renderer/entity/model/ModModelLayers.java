package thefloydman.linkingbooks.client.renderer.entity.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import thefloydman.linkingbooks.util.Reference;

public class ModModelLayers {

    public static final ModelLayerLocation COVER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "main"),
            "cover");
    public static final ModelLayerLocation PAGES = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "main"),
            "pages");

}