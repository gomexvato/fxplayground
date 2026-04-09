package com.playground.components;

import javafx.scene.image.Image;

public class PFxImage {
    public static Image image(String name) {
        return new Image("images/"+name);
    }
}
