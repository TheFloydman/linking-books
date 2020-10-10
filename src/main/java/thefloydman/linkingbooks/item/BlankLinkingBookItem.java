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
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (!world.isRemote() || heldStack.getCount() > 1) {
            ItemStack writtenBook = LinkingUtils.createWrittenLinkingBook(player, heldStack);
            return ActionResult.resultPass(writtenBook);
        }
        return ActionResult.resultPass(heldStack);
    }

}
