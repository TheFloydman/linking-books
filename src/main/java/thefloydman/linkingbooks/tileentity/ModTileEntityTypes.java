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

    public static final RegistryObject<TileEntityType<LinkingLecternTileEntity>> LINKING_LECTERN = TILE_ENTITIES
            .register(TileEntityNames.LINKING_LECTERN, () -> new TileEntityType<>(LinkingLecternTileEntity::new,
                    Sets.newHashSet(ModBlocks.LINKING_LECTERN.get()), null));

}
