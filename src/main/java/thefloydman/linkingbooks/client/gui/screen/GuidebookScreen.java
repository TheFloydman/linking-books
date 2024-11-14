/*******************************************************************************
 * Copyright 2019-2022 Dan Floyd ("TheFloydman")
 *
 * This file is part of Linking Books.
 *
 * Linking Books is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Linking Books is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Linking Books. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package thefloydman.linkingbooks.client.gui.screen;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import thefloydman.linkingbooks.client.gui.book.GuiBookManager;
import thefloydman.linkingbooks.client.gui.widget.BookWidget;
import thefloydman.linkingbooks.client.gui.widget.NestedWidget;
import thefloydman.linkingbooks.client.sound.ModSounds;
import thefloydman.linkingbooks.world.inventory.GuidebookMenuType;

import javax.annotation.Nonnull;

public class GuidebookScreen extends AbstractContainerScreen<GuidebookMenuType> {

    public GuidebookScreen(GuidebookMenuType menuType, Inventory intentory, Component narration) {
        super(menuType, intentory, narration);
        this.imageWidth = 256;
        this.imageHeight = 180;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new BookWidget("guidebook", this.leftPos, this.topPos, 100.0F, this.imageWidth,
                this.imageHeight, Component.literal("Guidebook"), this, 1.0F, this.font,
                GuiBookManager.getPages().values().stream().collect(Collectors.toList())));
    }

    @Override
    public void onClose() {
        this.minecraft.player.playSound(ModSounds.BOOK_CLOSE.get());
        super.onClose();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        Map<String, NestedWidget> backupList = Maps.newHashMap(this.renderables.stream()
                .filter(item -> item instanceof NestedWidget).map(item -> NestedWidget.class.cast(item))
                .collect(Collectors.toMap(NestedWidget::getId, Function.identity())));
        this.renderables.clear();
        super.resize(minecraft, width, height);
        for (NestedWidget element : this.renderables.stream().filter(item -> item instanceof NestedWidget)
                .map(item -> NestedWidget.class.cast(item)).collect(Collectors.toList())) {
            if (backupList.get(element.getId()) != null) {
                element.restore(backupList.get(element.getId()));
                element.restoreChildren(backupList.get(element.getId()));
            }
        }

    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int p_97809_, int p_97810_) {
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float p_97788_, int p_97789_, int p_97790_) {
    }

}