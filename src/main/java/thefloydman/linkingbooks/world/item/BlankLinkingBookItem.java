package thefloydman.linkingbooks.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thefloydman.linkingbooks.util.LinkingUtils;

public class BlankLinkingBookItem extends Item {

    public BlankLinkingBookItem(Properties properties) {
        super(properties);
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide() && !heldStack.isEmpty()) {
            ItemStack writtenBook = LinkingUtils.createWrittenLinkingBook(player, heldStack);
            if (heldStack.getCount() > 1) {
                player.addItem(writtenBook);
                heldStack.shrink(1);
            } else {
                return InteractionResultHolder.pass(writtenBook);
            }
        }
        return InteractionResultHolder.pass(heldStack);
    }


}