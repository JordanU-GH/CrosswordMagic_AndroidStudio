package edu.jsu.mcis.cs408.crosswordmagic.controller;

public class CrosswordMagicController extends AbstractController {

    public static final String TEST_PROPERTY = "TestProperty";

    public void getTestProperty(String value) {
        getModelProperty(TEST_PROPERTY);
    }

}