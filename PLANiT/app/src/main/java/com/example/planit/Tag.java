package com.example.planit;

import java.util.UUID;

public class Tag {
    final static int MAX_TAG_LENGTH = 20;
    final static String ACCENT_COLOR = "#555";
    private String displayString;
    private String hexColor;
    private UUID uuid;

    public Tag(String displayString, String hexColor) throws IllegalArgumentException {
        if (displayString.length() == 0 || displayString.length() > MAX_TAG_LENGTH)
            throw new IllegalArgumentException("Illegal display string length");
        this.displayString = displayString;
        this.hexColor = hexColor;
        this.uuid = UUID.randomUUID();
    }

    public Tag(String displayString) throws IllegalArgumentException {
        if (displayString.length() == 0 || displayString.length() > MAX_TAG_LENGTH)
            throw new IllegalArgumentException("Illegal display string length");
        this.displayString = displayString;
        this.hexColor = ACCENT_COLOR;
        this.uuid = UUID.randomUUID();
    }

    public String getDisplayString() {
        return displayString;
    }

    public void setDisplayString(String displayString) throws IllegalArgumentException {
        if (displayString.length() == 0 || displayString.length() > MAX_TAG_LENGTH)
            throw new IllegalArgumentException("Illegal display string length");
        this.displayString = displayString;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) { this.hexColor = hexColor; }

    public UUID getUUID() { return uuid; }
}
