package com.playground;

import javafx.scene.Node;

public interface ComponentVariant {
    String getTitle();
    String getDescription();
    Node getComponent();

    default int getMinHeight() { return 200; }
}

