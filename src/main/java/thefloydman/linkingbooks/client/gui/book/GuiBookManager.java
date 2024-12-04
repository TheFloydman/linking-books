/*
 * This file is part of Linking Books, a mod for Minecraft.
 * Copyright (c) 2019-2024 Dan Floyd ("TheFloydman").
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package thefloydman.linkingbooks.client.gui.book;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import thefloydman.linkingbooks.Reference;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class GuiBookManager implements ResourceManagerReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_RESET = "§r";
    private static final Map<String, String> FORMAT_MAP = Maps.newHashMap();
    private static final Map<String, String> COLOR_MAP = Maps.newHashMap();
    private static Map<Integer, List<Object>> guidebookPages = Maps.newHashMap();

    static {
        COLOR_MAP.put("black", "0");
        COLOR_MAP.put("dark_blue", "1");
        COLOR_MAP.put("dark_green", "2");
        COLOR_MAP.put("dark_aqua", "3");
        COLOR_MAP.put("dark_red", "4");
        COLOR_MAP.put("dark_purple", "5");
        COLOR_MAP.put("gold", "6");
        COLOR_MAP.put("gray", "7");
        COLOR_MAP.put("dark_gray", "8");
        COLOR_MAP.put("blue", "9");
        COLOR_MAP.put("green", "a");
        COLOR_MAP.put("aqua", "b");
        COLOR_MAP.put("red", "c");
        COLOR_MAP.put("light_purple", "d");
        COLOR_MAP.put("yellow", "e");
        COLOR_MAP.put("white", "f");
        FORMAT_MAP.put("obfuscated", "k");
        FORMAT_MAP.put("bold", "l");
        FORMAT_MAP.put("strikethrough", "m");
        FORMAT_MAP.put("underline", "n");
        FORMAT_MAP.put("italic", "o");
    }

    public static Map<Integer, List<Object>> getPages() {
        return guidebookPages;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        guidebookPages = Maps.newHashMap();
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected();
        Collection<ResourceLocation> guidebookResources = resourceManager
                .listResources("lang/linkingbooks/guidebook", resourceLocation ->
                        resourceLocation.getPath().endsWith(String.format("%s.xml", currentLang))
                ).keySet();
        for (ResourceLocation resourceLocation : guidebookResources) {
            Document document = null;
            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Optional<Resource> optionalResource = resourceManager.getResource(Reference.getAsResourceLocation("lang/linkingbooks/guidebook/schema.xsd"));
                if (optionalResource.isEmpty()) {
                    continue;
                }
                Schema schema = factory.newSchema(new StreamSource(optionalResource.get().open()));
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setSchema(schema);
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                document = builder.parse(resourceManager.open(resourceLocation));
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            if (document != null) {
                NodeList pageNodes = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < pageNodes.getLength(); i++) {
                    Node pageNode = pageNodes.item(i);
                    if (pageNode.getNodeType() == Node.ELEMENT_NODE && pageNode.getNodeName().equals("page")) {
                        List<Object> elementList = Lists.newArrayList();
                        NodeList elementNodes = pageNode.getChildNodes();
                        for (int j = 0; j < elementNodes.getLength(); j++) {
                            Node elementNode = elementNodes.item(j);
                            if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                                switch (elementNode.getNodeName()) {
                                    case "p" -> {
                                        List<String> brokenLines = Lists.newArrayList();
                                        NodeList paragraphChildNodes = elementNode.getChildNodes();
                                        for (int k = 0; k < paragraphChildNodes.getLength(); k++) {
                                            Node paragraphChildNode = paragraphChildNodes.item(k);
                                            if (paragraphChildNode.getNodeName().equals("style")) {
                                                String colorCode = "";
                                                StringBuilder formatCode = new StringBuilder();
                                                if (paragraphChildNode.hasAttributes()) {
                                                    NamedNodeMap styleNodeMap = paragraphChildNode.getAttributes();
                                                    for (int l = 0; l < styleNodeMap.getLength(); l++) {
                                                        Node attribute = styleNodeMap.item(l);
                                                        if (attribute.getNodeValue() != null) {
                                                            if (attribute.getNodeName().equals("color")) {
                                                                String color = COLOR_MAP.get(attribute.getNodeValue());
                                                                colorCode = color == null ? "" : "§" + color;
                                                            } else {
                                                                if (attribute.getNodeValue().equals("true")) {
                                                                    String format = FORMAT_MAP.get(attribute.getNodeName());
                                                                    formatCode.append(format == null ? "" : "§" + format);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    paragraphChildNode.setTextContent(colorCode + formatCode
                                                            + paragraphChildNode.getTextContent() + TAG_RESET);
                                                }
                                            } else if (paragraphChildNode.getNodeName().equals("br")) {
                                                paragraphChildNode.setTextContent("/u000a");
                                            }
                                        }
                                        brokenLines = Stream.of(elementNode.getTextContent().split("/u000a")).toList();
                                        GuiBookParagraph paragraph = new GuiBookParagraph(brokenLines);
                                        elementList.add(paragraph);
                                    }
                                    case "img" -> {
                                        NamedNodeMap imageNodeMap = elementNode.getAttributes();
                                        ResourceLocation location = ResourceLocation.parse(
                                                imageNodeMap.getNamedItem("src").getNodeValue());
                                        float scale = imageNodeMap.getNamedItem("scale") == null ? 1.0F
                                                : Float.parseFloat(imageNodeMap.getNamedItem("scale").getNodeValue());
                                        int width = imageNodeMap.getNamedItem("width") == null ? 256
                                                : Integer.parseInt(imageNodeMap.getNamedItem("width").getNodeValue());
                                        int height = imageNodeMap.getNamedItem("height") == null ? 256
                                                : Integer.parseInt(imageNodeMap.getNamedItem("height").getNodeValue());
                                        elementList.add(new GuiBookImage(location, scale, width, height));
                                    }
                                    case "recipe" -> {
                                        if (elementNode.hasAttributes()) {
                                            NamedNodeMap attributes = elementNode.getAttributes();
                                            String source = attributes.getNamedItem("src") == null ? ""
                                                    : attributes.getNamedItem("src").getNodeValue();
                                            elementList.add(new GuiBookRecipe(RecipeType.CRAFTING,
                                                    ResourceLocation.tryParse(source)));
                                        }
                                    }
                                    case "recipes" -> {
                                        if (elementNode.hasChildNodes()) {
                                            NodeList recipeNodeList = elementNode.getChildNodes();
                                            List<ResourceLocation> recipeLocationList = Lists.newArrayList();
                                            for (int k = 0; k < recipeNodeList.getLength(); k++) {
                                                Node recipeNode = recipeNodeList.item(k);
                                                if (recipeNode.getNodeType() == Node.ELEMENT_NODE
                                                        && recipeNode.getNodeName().equals("rec")) {
                                                    if (recipeNode.hasAttributes()) {
                                                        NamedNodeMap attributes = recipeNode.getAttributes();
                                                        String source = attributes.getNamedItem("src") == null ? ""
                                                                : attributes.getNamedItem("src").getNodeValue();
                                                        recipeLocationList.add(ResourceLocation.tryParse(source));
                                                    }
                                                }
                                            }
                                            elementList.add(
                                                    new GuiBookRecipeCarousel(RecipeType.CRAFTING, recipeLocationList));
                                        }
                                    }
                                }
                            }
                        }
                        guidebookPages.put(guidebookPages.size(), elementList);
                    }
                }
            }
        }
        LOGGER.info("Successfully imported {} guidebook pages for Linking Books.", guidebookPages.size());
    }
}