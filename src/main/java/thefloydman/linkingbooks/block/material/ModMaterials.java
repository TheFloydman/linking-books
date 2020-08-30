package thefloydman.linkingbooks.block.material;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

public class ModMaterials {

    public static final Material INK = new Material(MaterialColor.BLACK, true, false, false, true, true, false,
            PushReaction.DESTROY);

}
