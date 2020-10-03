package thefloydman.linkingbooks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thefloydman.linkingbooks.item.WrittenLinkingBookItem;
import thefloydman.linkingbooks.tileentity.BookDisplayTileEntity;
import thefloydman.linkingbooks.tileentity.ModTileEntityTypes;

public class BookDisplayBlock extends LecternBlock {

    public BookDisplayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.BOOK_DISPLAY.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, BlockRayTraceResult result) {
        TileEntity generic = world.getTileEntity(pos);
        if (generic instanceof BookDisplayTileEntity) {
            BookDisplayTileEntity tileEntity = (BookDisplayTileEntity) generic;
            if (!world.isRemote() && hand.equals(Hand.MAIN_HAND) && tileEntity.hasBook() && !player.isSneaking()) {
                ItemStack stack = tileEntity.getBook();
                Item item = stack.getItem();
                if (item instanceof WrittenLinkingBookItem) {
                    WrittenLinkingBookItem book = (WrittenLinkingBookItem) item;
                    book.onItemRightClick(world, player, hand);
                }
            }
        }
        return ActionResultType.PASS;
    }

}
