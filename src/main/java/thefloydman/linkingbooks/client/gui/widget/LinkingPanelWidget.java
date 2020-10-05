package thefloydman.linkingbooks.client.gui.widget;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thefloydman.linkingbooks.network.ModNetworkHandler;
import thefloydman.linkingbooks.network.packets.LinkMessage;

@OnlyIn(Dist.CLIENT)
public class LinkingPanelWidget extends NestedWidget {

    public String dimension = "minecraft:overworld";
    public BlockPos blockPos = new BlockPos(0, 0, 0);
    public float rotation = 0.0F;
    public List<String> linkEffects = new ArrayList<String>();

    public LinkingPanelWidget(int x, int y, float zLevel, int width, int height, ITextComponent narration,
            String dimension, BlockPos pos, float rotation, List<String> linkEffects) {
        super(x, y, width, height, narration);
        this.dimension = dimension;
        this.blockPos = pos;
        this.rotation = rotation;
        this.linkEffects = linkEffects;
    }

    @Override
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.field_230694_p_) {
            return;
        }
        this.fill(matrixStack, this.field_230690_l_, this.field_230691_m_, this.field_230690_l_ + this.field_230688_j_,
                this.field_230691_m_ + this.field_230689_k_, Color.BLACK.getRGB());

        this.renderChildren(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onMouseClickChildren(double mouseX, double mouseY) {
        ModNetworkHandler.sendToServer(new LinkMessage(this.dimension, this.blockPos, this.rotation, this.linkEffects));
        super.onMouseClickChildren(mouseX, mouseY);
    }

}
