package thefloydman.linkingbooks.ink.gson;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ColorTypeAdapter extends TypeAdapter<Color> {

    @Override
    public void write(JsonWriter writer, Color color) throws IOException {
        if (color == null) {
            writer.nullValue();
            return;
        }
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        writer.name("red").value(red);
        writer.name("green").value(green);
        writer.name("blue").value(blue);
        writer.name("alpha").value(alpha);
    }

    @Override
    public Color read(JsonReader reader) throws IOException {
        if (reader.peek().equals(JsonToken.NULL)) {
            reader.nextNull();
            return null;
        }
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 255;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("red")) {
                red = reader.nextInt();
            } else if (name.equals("green")) {
                green = reader.nextInt();
            } else if (name.equals("blue")) {
                blue = reader.nextInt();
            } else if (name.equals("alpha")) {
                alpha = reader.nextInt();
            }
        }
        return new Color(red, green, blue, alpha);
    }

}
