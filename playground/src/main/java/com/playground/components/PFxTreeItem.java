package com.playground.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

class PFxTreeItem extends TreeItem {
    private ObjectProperty userDataProperty = new SimpleObjectProperty();

    PFxTreeItem(String text, Object userData) {
        super(text);
        this.userDataProperty.set(userData);
    }

    PFxTreeItem(String text) {
        this(text, null);
    }

    Object getUserData() {
        return userDataProperty.get();
    }
}
