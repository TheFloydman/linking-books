package thefloydman.linkingbooks.tileentity;

import com.google.common.collect.Sets;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.util.Reference;
import thefloydman.linkingbooks.util.Reference.TileEntityNames;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister
            .create(ForgeRegistries.TILE_ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<TileEntityType<InkMixerTileEntity>> INK_MIXER = TILE_ENTITIES.register(
            TileEntityNames.INK_MIXER,
            () -> new TileEntityType<>(InkMixerTileEntity::new, Sets.newHashSet(ModBlocks.INK_MIXER.get()), null));

}
