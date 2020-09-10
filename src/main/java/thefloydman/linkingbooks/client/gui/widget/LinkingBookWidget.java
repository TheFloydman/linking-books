package thefloydman.linkingbooks.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.util.Reference;

@OnlyIn(Dist.CLIENT)
public class LinkingBookWidget extends NestedWidget {

    private static final ResourceLocation COVER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_cover.png");
    private static final ResourceLocation PAPER_TEXTURE = Reference
            .getAsResourceLocation("textures/gui/linkingbook/linking_book_paper.png");

    public LinkingBookWidget(int x, int y, int width, int height, ITextComponent narration, NestedWidget parent) {
        super(x, y, width, height, narration, parent);
        this.addChild(new LinkingPanelWidget(this.field_230690_l_ + 155, this.field_230691_m_ + 41, 64, 42,
                new StringTextComponent("Linking Panel"), this));
    }

    @Override
    public void renderThis(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.push();
        RenderSystem.pushMatrix();

        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE,
                DestFactor.ZERO);
        Minecraft.getInstance().getTextureManager().bindTexture(COVER_TEXTURE);
        float color[] = DyeColor.GREEN.getColorComponentValues();
        RenderSystem.color4f(MathHelper.clamp(color[0], 0.1F, 1.0F), MathHelper.clamp(color[1], 0.1F, 1.0F),
                MathHelper.clamp(color[2], 0.1F, 1.0F), 1.0F);
        this.func_238474_b_(matrixStack, this.field_230690_l_, this.field_230691_m_, 0, 0, this.field_230688_j_,
                this.field_230689_k_);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(PAPER_TEXTURE);
        this.func_238474_b_(matrixStack, this.field_230690_l_, this.field_230691_m_, 0, 0, this.field_230688_j_,
                this.field_230689_k_);

        RenderSystem.popMatrix();
        matrixStack.pop();
    }

}
