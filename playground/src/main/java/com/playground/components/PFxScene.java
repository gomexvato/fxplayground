package com.playground.components;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class PFxScene extends Scene {
    protected static String[] STYLESHEETS = {"styles/FxComponents.css"};

    public PFxScene(Parent root, int width, int height, String[] styles) {
        super(root, width, height);
        getStylesheets().addAll(styles);
    }

    public PFxScene(Parent root, int width, int height) {
        this(root, width, height, STYLESHEETS);
    }

    public PFxScene(Parent root, String[] styles) {
        super(root);
        getStylesheets().addAll(styles);
    }

    public PFxScene(Parent root) {
        this(root, STYLESHEETS);
    }
}
