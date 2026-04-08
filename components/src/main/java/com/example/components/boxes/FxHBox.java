package com.example.components.boxes;

import com.example.components.FxDisposable;
import com.example.utils.FxListeners;
import com.example.utils.ObservableListUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Supplier;

public class FxHBox extends HBox implements FxDisposable {
    protected final FxListeners listeners = new FxListeners();

    public FxHBox(int spacing, String style) {
        super(spacing);
        if(style!=null) setStyle(style);
        listeners.add(disableProperty(), (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), "fx-disabled", nv));
    }

    public FxHBox(int spacing) {
        this(spacing, null);
    }

    public FxHBox(String style) {
        this(5, style);
    }

    public FxHBox() {
        this(5, null);
    }

    public FxHBox(int spacing, String style, Node... content) {
        this(spacing, style);
        getChildren().addAll(content);
        listeners.add(disableProperty(), (o, ov, nv) ->
                ObservableListUtils.addOrRemove(getStyleClass(), "fx-disabled", nv));
    }

    public FxHBox(Node... content) {
        this(5, null, content);
    }

    public void add(Node n) { getChildren().add(n); }
    public void add(Node... n) { getChildren().addAll(n); }

    // add conditionally -- node is always created
    public FxHBox add(boolean condition, Node node) {
        if(condition) { add(node); }
        return this;
    }

    // add conditionally -- node is only created when condition is met
    public FxHBox add(boolean condition, Supplier<Node> node) {
        if(condition) { add(node.get()); }
        return this;
    }

    public void remove(Node n) { getChildren().remove(n); }
    public void remove(Node... n) { getChildren().removeAll(n); }
    public void clear() { getChildren().removeAll(getChildren().toArray(new Node[0])); }

    public FxHBox setAllWidths(double value) {
        this.setPrefWidth(value);
        this.setMinWidth(value);
        this.setMaxWidth(value);
        return this;
    }

    public FxHBox setAllHeight(double value) {
        this.setPrefHeight(value);
        this.setMinHeight(value);
        this.setMaxHeight(value);
        return this;
    }

    public FxHBox size(double width, double height) {
        setAllWidths(width);
        return setAllHeight(height);
    }

    public FxHBox style(String style) {
        this.setStyle(style);
        return this;
    }

    public static FxHBox addRight(Node... nodes){
        FxHBox spacer = new FxHBox();
        FxHBox box = new FxHBox(spacer);
        box.getChildren().addAll(nodes);
        FxHBox.setHgrow(spacer, Priority.ALWAYS);
        return box;
    }

    public static FxHBox spacer() {
        FxHBox box = new FxHBox();
        FxHBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private static FxHBox column(Node node, IntegerProperty widthProperty, double percent, Pos pos) {
        FxHBox col = new FxHBox(node);
        col.setAlignment(pos);
        col.setMinWidth(widthProperty.get() * percent);
        col.prefWidthProperty().bind(widthProperty.multiply(percent));
        return col;
    }

    private static FxHBox column(Node node, IntegerProperty widthProperty, Pos pos) {
        FxHBox col = new FxHBox(node);
        col.setAlignment(pos);
        if(widthProperty!=null) {
            col.setMinWidth(widthProperty.get());
            col.setMaxWidth(widthProperty.get());
            widthProperty.addListener((o, ov, nv) -> col.setAllWidths(nv.intValue()));
        }
        return col;
    }

    public static FxHBox centered(Node node, IntegerProperty widthProperty, double percent) {
        return column(node, widthProperty, percent, Pos.CENTER);
    }

    public static FxHBox centered(Node node, IntegerProperty widthProperty) {
        return column(node, widthProperty, Pos.CENTER);
    }

    public static FxHBox centered(Node... nodes) {
        return column(nodes.length == 1 ? nodes[0] : new FxHBox(nodes), null, Pos.CENTER);
    }

    public static FxHBox leftAligned(Node node, IntegerProperty widthProperty, double percent) {
        return column(node, widthProperty, percent, Pos.CENTER_LEFT);
    }

    public static FxHBox leftAligned(Node node, IntegerProperty widthProperty) {
        return column(node, widthProperty, Pos.CENTER_LEFT);
    }

    public static FxHBox leftAligned(Node... nodes) {
        return column(nodes.length == 1 ? nodes[0] : new FxHBox(nodes), null, Pos.CENTER_LEFT);
    }

    public static FxHBox rightAligned(Node node, IntegerProperty widthProperty, double percent) {
        return column(node, widthProperty, percent, Pos.CENTER_RIGHT);
    }

    public static FxHBox rightAligned(Node node, IntegerProperty widthProperty) {
        return column(node, widthProperty, Pos.CENTER_RIGHT);
    }

    public static FxHBox rightAligned(Node... nodes) {
        return nodes.length == 1
                ? column(nodes[0], null, Pos.CENTER_RIGHT)
                : column(new FxHBox(nodes), null, Pos.CENTER_RIGHT);
    }

    public FxHBox spacing(int spacing) {
        setSpacing(spacing);
        return this;
    }

    public FxHBox addStyleClass(String clazz) {
        getStyleClass().add(clazz);
        return this;
    }

    public FxHBox visibleBind(ObservableValue<Boolean> prop) {
        visibleProperty().bind(prop);
        managedProperty().bind(visibleProperty());
        return this;
    }

    public <T> FxHBox addListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        listeners.add(observable, listener);
        return this;
    }

    public FxHBox debug() {
        return debug("red");
    }

    public FxHBox debug(String color) {
        setStyle("-fx-border-width: 1px; -fx-border-color: "+color);
        return this;
    }

    public void dispose() {
        listeners.remove();
        for(Node node: getChildren()) {
            if(node instanceof FxDisposable) {
                ((FxDisposable)node).dispose();
            }
        }
        getChildren().clear();
    }
}