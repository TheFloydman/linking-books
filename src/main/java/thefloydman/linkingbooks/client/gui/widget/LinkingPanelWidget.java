package thefloydman.linkingbooks.client.gui.widget;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.capability.LinkData;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.network.packets.LinkMessage;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    public boolean holdingBook = false;
    public ILinkData linkData = LinkData.LINK_DATA.getDefaultInstance();
    public boolean canLink = false;
    DynamicTexture linkingPanelImage = null;

    public LinkingPanelWidget(int x, int y, float zLevel, int width, int height, ITextComponent narration,
            boolean holdingBook, ILinkData linkData, boolean canLink, NativeImage linkingPanelImage) {
        super(x, y, width, height, narration);
        this.holdingBook = holdingBook;
        this.linkData = linkData;
        this.canLink = canLink;
        if (linkingPanelImage != null) {
            NativeImage image256 = new NativeImage(256, 256, false);
            for (int textureY = 0; textureY < linkingPanelImage.getHeight(); textureY++) {
                for (int textureX = 0; textureX < linkingPanelImage.getWidth(); textureX++) {
                    image256.setPixelRGBA(textureX, textureY, linkingPanelImage.getPixelRGBA(textureX, textureY));
                }
            }
            this.linkingPanelImage = new DynamicTexture(image256);
        }
    }

    @Override
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.field_230694_p_) {
            return;
        }
        int panelColor = this.canLink ? new Color(32, 192, 255).getRGB() : new Color(192, 192, 192).getRGB();
        this.fill(matrixStack, this.field_230690_l_, this.field_230691_m_, this.field_230690_l_ + this.field_230688_j_,
                this.field_230691_m_ + this.field_230689_k_, panelColor);

        if (this.canLink) {
            if (this.linkingPanelImage != null) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.linkingPanelImage.bindTexture();
                this.func_238474_b_(matrixStack, this.field_230690_l_, this.field_230691_m_, 0, 0,
                        this.linkingPanelImage.getTextureData().getWidth(),
                        this.linkingPanelImage.getTextureData().getHeight());
            }
        }

        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onMouseClickChildren(double mouseX, double mouseY) {
        ModNetworkHandler.sendToServer(new LinkMessage(this.holdingBook, this.linkData));
        super.onMouseClickChildren(mouseX, mouseY);
    }

}
