package com.example.components.submittables;

import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

final class LockIcon extends StackPane {

    LockIcon() {
        Region lockShape = new Region();
        lockShape.getStyleClass().add("fx-lock-icon");
        lockShape.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        StackPane.setAlignment(lockShape, Pos.CENTER_LEFT);
        getChildren().add(lockShape);
    }
}