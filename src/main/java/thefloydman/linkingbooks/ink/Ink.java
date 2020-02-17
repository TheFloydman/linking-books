package thefloydman.linkingbooks.ink;

import java.awt.Color;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import thefloydman.linkingbooks.ink.gson.ColorTypeAdapter;

public class Ink {

    @SerializedName("mod_id")
    protected final String modId;

    @SerializedName("ink_id")
    protected final String inkId;

    @SerializedName("color")
    @JsonAdapter(ColorTypeAdapter.class)
    protected final Color color;

    public Ink(String modId, String inkId, Color color) {
        this.modId = modId;
        this.inkId = inkId;
        this.color = color;
    }

    public Ink(String modId, String inkId, int red, int green, int blue, int alpha) {
        this(modId, inkId, new Color(red, green, blue, alpha));
    }

}
