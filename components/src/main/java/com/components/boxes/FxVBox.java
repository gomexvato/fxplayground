package com.components.boxes;

import com.components.Fx;
import com.components.FxDisposable;
import com.utils.FxListeners;
import com.utils.ObservableListUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Supplier;

public class FxVBox extends VBox implements FxDisposable {
    protected final FxListeners listeners = new FxListeners();

    public FxVBox(int spacing, String style) {
        this(spacing, style, false);
    }

    public FxVBox(int spacing) {
        this(spacing, null, false);
    }

    public FxVBox(String style) {
        this(5, style);
    }

    public FxVBox(String style, Node... content) {
        this(5, style, false, content);
    }

    public FxVBox(int spacing, Node... content) {
        this(spacing, null, false, content);
    }

    public FxVBox() {
        this(5, null, false);
    }

    public FxVBox(Node... content) {
        this(5, null, false, content);
    }

    public FxVBox(boolean separated, Node... content) {
        this(5, null, separated, content);
    }

    public FxVBox(int spacing, String style, boolean separated, Node... content) {
        super(spacing);
        if(style!=null) getStyleClass().add(style);
        listeners.add(disableProperty(), (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), "fx-disabled", nv));
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

    // adds conditionally - node is always created
    public FxVBox add(boolean condition, Node n) {
        if(condition) getChildren().add(n);
        return this;
    }

    // adds conditionally - node is only created when condition is met
    public FxVBox add(boolean condition, Supplier<Node> supplier) {
        if(condition) getChildren().add(supplier.get());
        return this;
    }

    public FxVBox add(Node n) { getChildren().add(n); return this; }
    public ObservableList remove(Node n) { getChildren().remove(n); return getChildren(); }
    public void add(Node... n) { getChildren().addAll(n); }
    public FxVBox add(String text, Node node) {
        add(Fx.row(text, node));
        return this;
    }
    public void remove(Node... n) { getChildren().removeAll(n); }
    public void clear() { getChildren().removeAll(getChildren().toArray(new Node[0])); }

    public FxVBox setAllWidths(double value) {
        this.setPrefWidth(value);
        this.setMinWidth(value);
        this.setMaxWidth(value);
        return this;
    }

    public FxVBox setAllHeights(double value) {
        this.setPrefHeight(value);
        this.setMinHeight(value);
        this.setMaxHeight(value);
        return this;
    }

    public FxVBox size(int width, int height) {
        setAllWidths(width);
        setAllHeights(height);
        return this;
    }

    public FxVBox style(String style) {
        this.setStyle(style);
        return this;
    }

    public FxVBox center() {
        this.setAlignment(Pos.CENTER);
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

    public static FxVBox separator() {
        return new FxVBox("fx-separator", new FxVBox("fx-separator-hr"));
    }

    public FxVBox spacing(int spacing) {
        this.setSpacing(spacing);
        return this;
    }

    public FxVBox addStyleClass(String... classes) {
        getStyleClass().addAll(classes);
        return this;
    }

    public FxVBox addStyleClass(String style, boolean predicate) {
        if(predicate) getStyleClass().add(style);
        return this;
    }

    public FxVBox debug() {
        return debug("red");
    }

    public FxVBox debug(String color) {
        setStyle("-fx-border-width: 1px; -fx-border-color: "+color);
        return this;
    }

    public FxVBox visibleBind(ObservableValue<Boolean> prop) {
        visibleProperty().bind(prop);
        managedProperty().bind(visibleProperty());
        return this;
    }

    public FxVBox disableBind(ObservableValue<Boolean> prop) {
        disableProperty().bind(prop);
        return this;
    }

    public FxVBox stacked(Pos pos) {
        StackPane.setAlignment(this, pos);
        return this;
    }

    public FxVBox hGrow() {
        return hGrow(true);
    }

    public FxVBox hGrow(boolean grow) {
        return hGrow(grow, null);
    }

    public FxVBox hGrow(boolean grow, Double width) {
        if(grow) {
            FxHBox.setHgrow(this, Priority.ALWAYS);
        } else if (width != null) {
            setAllWidths(width);
        }
        return this;
    }

    public <T> FxVBox addListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        listeners.add(observable, listener);
        return this;
    }

    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

    public void dispose() {
        listeners.remove();
        for(Object node: getChildren()) {
            if(node instanceof FxDisposable) {
                ((FxDisposable)node).dispose();
            }
        }
        getChildren().clear();
    }
}