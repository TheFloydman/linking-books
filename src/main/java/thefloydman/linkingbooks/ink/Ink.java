package thefloydman.linkingbooks.ink;

import java.awt.Color;

public class Ink {

    protected final Color color;

    public Ink(Color color) {
        this.color = color;
    }

    public Ink(int red, int green, int blue, int alpha) {
        this(new Color(red, green, blue, alpha));
    }

    public Color getColor() {
        return this.color;
    }

}