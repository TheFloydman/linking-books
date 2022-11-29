/*******************************************************************************
 * Linking Books
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.client.gui.screen;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import thefloydman.linkingbooks.client.gui.widget.BookWidget;
import thefloydman.linkingbooks.client.gui.widget.NestedWidget;
import thefloydman.linkingbooks.client.resources.guidebook.GuidebookManager;
import thefloydman.linkingbooks.client.sound.ModSounds;

public class GuidebookScreen extends Screen {

    public GuidebookScreen(Component narration) {
        super(narration);
    }

    @Override
    protected void init() {
        super.init();
        int width = 256;
        int height = 192;
        int left = (this.width - width) / 2;
        int top = (this.height - height) / 2;
        this.addRenderableWidget(
                new BookWidget("guidebook", left, top, 0.0F, width, height, Component.literal("Guidebook"), this.font,
                        GuidebookManager.getPages().values().stream().collect(Collectors.toList())));
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

}
