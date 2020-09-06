package thefloydman.linkingbooks.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import thefloydman.linkingbooks.client.gui.widget.LinkingPanel;

public class LinkingBookScreen extends Screen {

    public LinkingBookScreen(ITextComponent narration) {
        super(narration);
    }

    @Override
    protected void func_231160_c_() {
        this.func_230480_a_(new LinkingPanel(0, 0, 200, 20, new StringTextComponent("Linking Panel")));
    }

}
