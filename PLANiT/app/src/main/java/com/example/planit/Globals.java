package com.example.planit;

public class Globals{
    private static Globals globals_instance = null;
    final int MAX_TITLE_LENGTH;
    final int MAX_TEXT_LENGTH;
    final int MAX_TAG_NAME_LENGTH;

    private Globals(){
        MAX_TITLE_LENGTH = 50;
        MAX_TEXT_LENGTH = 500;
        MAX_TAG_NAME_LENGTH = 20;
    }

    public static Globals getInstance(){
        if (globals_instance == null)
            globals_instance = new Globals();
        return globals_instance;
    }
}