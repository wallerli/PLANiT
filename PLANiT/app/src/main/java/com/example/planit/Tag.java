package com.example.planit;

import android.graphics.Color;

import java.util.UUID;

public class Tag {
    private String name;
    private int hexColor = -1;   // The accent color feels confusing with custom tag colors
//    private int hexColor = R.attr.colorAccent;
    private final UUID uuid = UUID.randomUUID();

    public Tag(String name, int hexColor) {
        this.setName(name);
        this.hexColor = hexColor;
    }

    public Tag(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 0 for correct name length; 1 for empty name; 2 for exceeding max length
     */
    public int validateName(String name) {
        return (name.length() == 0) ? 1 : (name.length() > R.dimen.max_tag_length) ? 2 : 0;
    }

    public int getHexColor() {
        return hexColor;
    }

    public void setHexColor(int hexColor) {
        this.hexColor = hexColor;
    }

    public UUID getUUID() {
        return uuid;
    }
}
