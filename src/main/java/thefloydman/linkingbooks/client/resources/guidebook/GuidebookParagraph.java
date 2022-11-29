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
package thefloydman.linkingbooks.client.resources.guidebook;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class GuidebookParagraph {

    public String raw = "";
    public List<Component> renderable = Lists.newArrayList();

    public GuidebookParagraph(String contents) {
        this.raw = contents;
    }

    public void makeRenderable(Font font, float width) {
        List<Component> outputParagraph = Lists.newArrayList();
        List<String> wordList = Stream.of(this.raw.split(" ")).collect(Collectors.toList());
        boolean excessWords = true;
        boolean styled = false;
        String style = "";
        String lastStyle = "";
        while (excessWords) {
            List<String> lineList = Lists.newArrayList();
            int index = 0;
            excessWords = false;
            String postWord = "";
            for (String currentLine = ""; index < wordList.size(); index++) {
                String preWord = wordList.get(index);
                String temp = preWord;
                while (temp.indexOf("§") >= 0) {
                    int currentIndex = temp.indexOf("§");
                    String sub = temp.substring(currentIndex, currentIndex + 2);
                    styled = !sub.equals("§r");
                    if (styled) {
                        style += sub;
                    } else {
                        lastStyle = style;
                        style = "";
                    }
                    temp = currentIndex + 2 < temp.length() ? temp.substring(currentIndex + 2) : "";
                }
                postWord = (index == 0 && styled ? style : "") + preWord;
                lineList.add(postWord);
                currentLine = lineList.stream().collect(Collectors.joining(" "));
                excessWords = font.width(currentLine) > width;
                if (excessWords) {
                    if (preWord.contains("§r"))
                        postWord = lastStyle + preWord;
                    wordList.set(index, postWord);
                    lineList.remove(lineList.size() - 1);
                    break;
                }
            }
            outputParagraph.add(Component.literal(lineList.stream().collect(Collectors.joining(" "))));
            if (excessWords) {
                wordList = wordList.subList(index, wordList.size());
            }
        }
        this.renderable = outputParagraph;
    }

}
