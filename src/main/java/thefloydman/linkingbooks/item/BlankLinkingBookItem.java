package thefloydman.linkingbooks.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import thefloydman.linkingbooks.util.LinkingUtils;

public class BlankLinkingBookItem extends LinkingBookItem {

    public BlankLinkingBookItem(DyeColor color, Properties properties) {
        super(color, properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (!world.isRemote()) {
            if (!player.abilities.isCreativeMode) {
                heldStack.shrink(1);
            }
            ItemStack writtenBook = LinkingUtils.createWrittenLinkingBook(player, heldStack.getItem());
            if (!player.addItemStackToInventory(writtenBook)) {
                player.dropItem(writtenBook, false);
            }
        }
        return ActionResult.resultPass(heldStack);
    }

}
