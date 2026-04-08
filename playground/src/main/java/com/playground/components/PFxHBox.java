package com.playground.components;

import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.playground.utils.ObservableListUtils;

public class PFxHBox extends HBox {
    public PFxHBox(int spacing, String style) {
        super(spacing);
        if(style!=null) setStyle(style);
        disableProperty().addListener(
            (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), "fx-disabled", nv));
    }

    public PFxHBox(int spacing) {
        this(spacing, null);
    }

    public PFxHBox(String style) {
        this(5, style);
    }

    public PFxHBox() {
        this(5, null);
    }

    public PFxHBox(int spacing, String style, Node... content) {
        this(spacing, style);
        getChildren().addAll(content);
        disableProperty().addListener(
            (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), "fx-disabled", nv));
    }

    public PFxHBox(Node... content) {
        this(5, null, content);
    }

    public void add(Node n) { getChildren().add(n); }
    public void add(Node... n) { getChildren().addAll(n); }
    public void remove(Node n) { getChildren().remove(n); }
    public void remove(Node... n) { getChildren().removeAll(n); }
    public void clear() { getChildren().removeAll(getChildren().toArray(new Node[0])); }

    public PFxHBox setAllWidths(double value) {
        this.setPrefWidth(value);
        this.setMinWidth(value);
        this.setMaxWidth(value);
        return this;
    }

    public PFxHBox style(String style) {
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

    private static PFxHBox column(Node node, IntegerProperty widthProperty, double percent, Pos pos) {
        PFxHBox col = new PFxHBox(node);
        col.setAlignment(pos);
        col.prefWidthProperty().bind(widthProperty.multiply(percent));
        return col;
    }

    private static PFxHBox column(Node node, IntegerProperty widthProperty, Pos pos) {
        PFxHBox col = new PFxHBox(node);
        col.setAlignment(pos);
        if(widthProperty!=null) col.prefWidthProperty().bind(widthProperty);
        return col;
    }

    public static PFxHBox centered(Node node, IntegerProperty widthProperty, double percent) {
        return column(node, widthProperty, percent, Pos.CENTER);
    }

    public static PFxHBox centered(Node node, IntegerProperty widthProperty) {
        return column(node, widthProperty, Pos.CENTER);
    }

    public static PFxHBox centered(Node... nodes) {
        return column(nodes.length == 1 ? nodes[0] : new PFxHBox(nodes), null, Pos.CENTER);
    }

    public static PFxHBox leftAligned(Node node, IntegerProperty widthProperty, double percent) {
        return column(node, widthProperty, percent, Pos.CENTER_LEFT);
    }

    public static PFxHBox leftAligned(Node node, IntegerProperty widthProperty) {
        return column(node, widthProperty, Pos.CENTER_LEFT);
    }

    public static PFxHBox leftAligned(Node... nodes) {
        return column(nodes.length == 1 ? nodes[0] : new PFxHBox(nodes), null, Pos.CENTER_LEFT);
    }

    public static PFxHBox rightAligned(Node node, IntegerProperty widthProperty, double percent) {
        return column(node, widthProperty, percent, Pos.CENTER_RIGHT);
    }

    public static PFxHBox rightAligned(Node node, IntegerProperty widthProperty) {
        return column(node, widthProperty, Pos.CENTER_RIGHT);
    }

    public static PFxHBox rightAligned(Node... nodes) {
        return nodes.length == 1
                ? column(nodes[0], null, Pos.CENTER_RIGHT)
                : column(new PFxHBox(nodes), null, Pos.CENTER_RIGHT);
    }

    public PFxHBox withSpacing(int spacing) {
        setSpacing(spacing);
        return this;
    }

    public PFxHBox addStyleClass(String clazz) {
        getStyleClass().add(clazz);
        return this;
    }
}