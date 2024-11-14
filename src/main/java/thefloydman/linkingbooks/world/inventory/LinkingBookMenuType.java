package thefloydman.linkingbooks.world.inventory;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import thefloydman.linkingbooks.data.LinkData;
import thefloydman.linkingbooks.util.ImageUtils;

import javax.annotation.Nonnull;

public class LinkingBookMenuType extends AbstractContainerMenu {

    public boolean holdingBook = false;
    public int bookColor = DyeColor.GREEN.getFireworkColor();
    public LinkData linkData = LinkData.EMPTY;
    public boolean canLink = false;
    public NativeImage linkingPanelImage = new NativeImage(16, 16, false);

    public LinkingBookMenuType(int windowId, Inventory playerInventory) {
        super(ModMenuTypes.LINKING_BOOK.get(), windowId);
    }

    public LinkingBookMenuType(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.LINKING_BOOK.get(), containerId);
        this.holdingBook = extraData.readBoolean();
        this.bookColor = extraData.readInt();
        this.linkData = extraData.readJsonWithCodec(LinkData.CODEC);
        this.canLink = extraData.readBoolean();
        this.linkingPanelImage = extraData.readJsonWithCodec(ImageUtils.NATIVE_IMAGE_CODEC);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

}