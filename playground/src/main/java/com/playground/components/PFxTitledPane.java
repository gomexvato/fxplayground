package com.playground.components;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class PFxTitledPane extends TitledPane {
    public PFxTitledPane(Node title, Node content) {
        setGraphic(title);
        setContent(content);
    }
}
