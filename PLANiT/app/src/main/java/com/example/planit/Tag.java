package com.example.planit;

import java.util.UUID;

public class Tag {
    final static int MAX_TAG_NAME_LENGTH = 20;
    final static String ACCENT_COLOR = "#555";
    private String name;
    private String hexColor;
    private final UUID uuid;

    public Tag(String name, String hexColor) {
        if (name.length() == 0 || name.length() > MAX_TAG_NAME_LENGTH)
            throw new IllegalArgumentException("Tag name length not in range: " + 1 + "-" + MAX_TAG_NAME_LENGTH);
        this.name = name;
        this.hexColor = hexColor;
        this.uuid = UUID.randomUUID();
    }

    public Tag(String name) {
        if (name.length() == 0 || name.length() > MAX_TAG_NAME_LENGTH)
            throw new IllegalArgumentException("Tag name length not in range: " + 1 + "-" + MAX_TAG_NAME_LENGTH);
        this.name = name;
        this.hexColor = ACCENT_COLOR;
        this.uuid = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() == 0 || name.length() > MAX_TAG_NAME_LENGTH)
            throw new IllegalArgumentException("Tag name length not in range: " + 1 + "-" + MAX_TAG_NAME_LENGTH);
        this.name = name;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public UUID getUUID() {
        return uuid;
    }
}
