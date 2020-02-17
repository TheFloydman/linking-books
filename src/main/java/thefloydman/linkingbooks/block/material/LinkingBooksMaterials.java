package thefloydman.linkingbooks.block.material;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

public class LinkingBooksMaterials {

    public static final Material INK = new Material(MaterialColor.BLACK, true, false, false, true, true, false, true,
            PushReaction.DESTROY);

}
