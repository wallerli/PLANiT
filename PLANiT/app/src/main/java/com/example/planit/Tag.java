package com.example.planit;

import java.util.UUID;

public class Tag {
    private String name;
    private int hexColor = R.attr.colorAccent;
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
        if (name.length() == 0 || name.length() > R.dimen.max_tag_length)
            throw new IllegalArgumentException("Tag name length not in range: " + 1 + "-" + R.dimen.max_tag_length);
        this.name = name;
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
