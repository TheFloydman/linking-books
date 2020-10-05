package thefloydman.linkingbooks.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.util.LinkingUtils;

public class WrittenLinkingBookItem extends LinkingBookItem {

    public WrittenLinkingBookItem(DyeColor color, Properties properties) {
        super(color, properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (!world.isRemote() && !player.isSneaking()) {
            ILinkData linkData = heldStack.getCapability(LinkData.LINK_DATA).orElse(null);
            if (linkData != null) {
                LinkingUtils.openLinkingBookGui((ServerPlayerEntity) player, true, this.color, linkData);
            }
        }
        return ActionResult.resultPass(heldStack);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new LinkData.Provider();
    }

}
