package com.components;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class FxScene extends Scene {
    protected static String[] STYLESHEETS = {"styles/FxComponents.css"};

    public FxScene(Parent root, int width, int height, String[] styles) {
        super(root, width, height);
        getStylesheets().addAll(styles);
    }

    public FxScene(Parent root, int width, int height) {
        this(root, width, height, STYLESHEETS);
    }

    public FxScene(Parent root, String[] styles) {
        super(root);
        getStylesheets().addAll(styles);
    }

    public FxScene(Parent root) {
        this(root, STYLESHEETS);
    }
}
