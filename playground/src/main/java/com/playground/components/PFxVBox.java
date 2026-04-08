package com.playground.components;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import com.playground.utils.ObservableListUtils;

public class PFxVBox extends VBox {

    public PFxVBox(int spacing, String style) {
        this(spacing, style, false);
    }

    public PFxVBox(int spacing) {
        this(spacing, null, false);
    }

    public PFxVBox(String style) {
        this(5, style);
    }

    public PFxVBox(String style, Node... content) {
        this(5, style, false, content);
    }

    public PFxVBox(int spacing, Node... content) {
        this(spacing, null, false, content);
    }

    public PFxVBox() {
        this(5, null, false);
    }

    public PFxVBox(Node... content) {
        this(5, null, false, content);
    }

    public PFxVBox(boolean separated, Node... content) {
        this(5, null, separated, content);
    }

    public PFxVBox(int spacing, String style, boolean separated, Node... content) {
        super(spacing);
        if(style!=null) getStyleClass().add(style);
        disableProperty().addListener(
            (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), "fx-disabled", nv));
        if(content!=null) {
            if(separated) {
                int idx = 0;
                for(Node n: content) {
                    add(n);
                    if(idx == 0) n.setStyle("-fx-padding: 5 0 0 0");
                    if(idx == content.length-1) {
                        n.setStyle("-fx-padding: 0 0 5 0");
                    } else {
                        add(separator());
                    }
                    idx++;
                }
            } else {
                getChildren().addAll(content);
            }
        }
    }

    public void add(Node n) { getChildren().add(n); }
    public ObservableList remove(Node n) { getChildren().remove(n); return getChildren(); }
    public void add(Node... n) { getChildren().addAll(n); }
    public void remove(Node... n) { getChildren().removeAll(n); }
    public void clear() { getChildren().removeAll(getChildren().toArray(new Node[0])); }

    public PFxVBox setAllWidths(double value) {
        this.setPrefWidth(value);
        this.setMinWidth(value);
        this.setMaxWidth(value);
        return this;
    }

    public PFxVBox style(String style) {
        this.setStyle(style);
        return this;
    }

    public static PFxHBox addRight(Node... nodes){
        PFxHBox spacer = new PFxHBox();
        PFxHBox box = new PFxHBox(spacer);
        box.getChildren().addAll(nodes);
        PFxHBox.setHgrow(spacer, Priority.ALWAYS);
        return box;
    }

    public static PFxHBox spacer() {
        PFxHBox box = new PFxHBox();
        PFxHBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    public static PFxVBox separator() {
        return new PFxVBox("fx-separator", new PFxVBox("fx-separator-hr"));
    }

    public PFxVBox withSpacing(int spacing) {
        this.setSpacing(spacing);
        return this;
    }

    public PFxVBox addStyleClass(String clazz) {
        getStyleClass().add(clazz);
        return this;
    }
}