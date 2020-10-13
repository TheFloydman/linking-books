package thefloydman.linkingbooks.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import thefloydman.linkingbooks.api.capability.IColorCapability;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.ColorCapability;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;

public class WrittenLinkingBookItem extends LinkingBookItem {

    public WrittenLinkingBookItem(DyeColor color, Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (!world.isRemote() && !player.isSneaking()) {
            ILinkData linkData = heldStack.getCapability(LinkData.LINK_DATA).orElse(null);
            IColorCapability color = heldStack.getCapability(ColorCapability.COLOR).orElse(null);
            if (linkData != null && color != null) {
                LinkingUtils.openLinkingBookGui((ServerPlayerEntity) player, true, color.getColor(), linkData,
                        world.func_234923_W_().func_240901_a_());
            }
        }
        return ActionResult.resultPass(heldStack);
    }

}
