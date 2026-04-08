package com.playground.components;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class PFxScrollPane extends ScrollPane {
    public PFxScrollPane(Node node) {
        super(node);
        setFitToHeight(true);
        setFitToWidth(true);
    }
}
