package com.components;

import com.utils.FxListeners;
import com.utils.ObservableListUtils;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class FxLabel extends Label implements FxDisposable {
    private static final String DISABLED_CLASS = "fx-disabled";
    private Tooltip tooltip = null;
    private final FxListeners listeners = new FxListeners();

    public enum Emphasis {NONE, BOLD, RED, ITALIC, LIGHT_RED, VERTICAL}

    public FxLabel(String text, String style, Emphasis emphasis) {
        super(text);
        getStyleClass().add("fx-label");
        if (emphasis.equals(Emphasis.ITALIC)) getStyleClass().add("fx-label-italic");
        if (emphasis.equals(Emphasis.BOLD)) getStyleClass().add("fx-label-bold");
        if (emphasis.equals(Emphasis.RED)) getStyleClass().add("fx-label-red");
        if (emphasis.equals(Emphasis.LIGHT_RED)) getStyleClass().add("fx-label-lightred");
        if (emphasis.equals(Emphasis.VERTICAL)) getStyleClass().add("fx-label-vertical");
        if (style != null) getStyleClass().add(style);
        listeners.add(disabledProperty(), (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), DISABLED_CLASS, nv));
    }

    public FxLabel(String text, String style) {
        this(text, style, Emphasis.NONE);
    }

    public FxLabel(String text) {
        this(text, null);
    }

    public static FxLabel h1(String text) { return new FxLabel(text, "fx-label-h1"); }
    public static FxLabel h2(String text) { return new FxLabel(text, "fx-label-h2"); }
    public static FxLabel h3(String text) { return new FxLabel(text, "fx-label-h3"); }
    public static FxLabel h4(String text) { return new FxLabel(text, "fx-label-h4"); }
    public static FxLabel p(String text) { return new FxLabel(text, "fx-label-p"); }
    public static FxLabel p(StringExpression text) {
        FxLabel label = new FxLabel(text.get(), "fx-label-p");
        label.textProperty().bind(text);
        return label;
    }
    public static FxLabel p(StringProperty text, String clazz) {
        FxLabel label = new FxLabel(text.get(), clazz);
        label.textProperty().bind(text);
        return label;
    }
    public static <T> FxLabel p(ReadOnlyObjectProperty<T> prop) {
        FxLabel label = new FxLabel(String.valueOf(prop.get()), "fx-label-p");
        label.textProperty().bind(prop.asString());
        return label;
    }
    public static <T> FxLabel p(ReadOnlyObjectProperty<T> prop, String clazz) {
        FxLabel label = new FxLabel(prop.get().toString(), clazz);
        label.textProperty().bind(prop.asString());
        return label;
    }
    public static FxLabel p(String text, Emphasis emphasis) {
        return new FxLabel(text, "fx-label-p", emphasis);
    }
    public static FxLabel p(ReadOnlyStringProperty prop, Emphasis emphasis) {
        FxLabel label = new FxLabel(prop.get().toString(), "fx-label-p", emphasis);
        label.textProperty().bind(prop);
        return label;
    }
    public static FxLabel p(BooleanExpression p, String trueStr, String falseStr) {
        FxLabel label = new FxLabel("");
        label.textProperty().bind(new StringBinding() {{ bind(p); }
            @Override protected String computeValue() { return p.get() ? trueStr : falseStr; }});
        return label;
    }
    public static FxLabel red(ReadOnlyStringProperty prop) { return FxLabel.p(prop, Emphasis.RED); }
    public static FxLabel red(String text) { return red(text, true); }
    public static FxLabel red(String text, boolean visible) {
        FxLabel label = new FxLabel(text, null, Emphasis.RED);
        label.setVisible(visible);
        return label;
    }
    public static FxLabel lightRed(String text) { return new FxLabel(text, null, Emphasis.LIGHT_RED); }
    public static FxLabel bold(String text) { return new FxLabel(text, null, Emphasis.BOLD); }
    public static FxLabel vertical(String text) { return new FxLabel(text, null, Emphasis.VERTICAL); }

    public static FxLabel wrapped(String text, int maxWidth) {
        FxLabel label = Fx.label(text);
        label.maxWidthProperty().set(maxWidth);
        label.wrapTextProperty().set(true);
        return label;
    }

    public FxLabel addStyleClass(String clazz) {
        getStyleClass().add(clazz);
        return this;
    }

    public FxLabel style(String style) {
        setStyle(style);
        return this;
    }

    public FxLabel setAllWidths(double width) {
        setMinWidth(width);
        setPrefWidth(width);
        setMaxWidth(width);
        return this;
    }

    public FxLabel setAllHeights(double height) {
        setMinHeight(height);
        setPrefHeight(height);
        setMaxHeight(height);
        return this;
    }

    public FxLabel size(double width, double height) {
        setAllWidths(width);
        setAllHeights(height);
        return this;
    }

    public FxLabel setDisabled() {
        setDisable(true);
        return this;
    }

    public FxLabel withWrappedText() {
        setWrapText(true);
        return this;
    }

    public FxLabel visibleBind(ObservableValue<Boolean> prop) {
        visibleProperty().bind(prop);
        managedProperty().bind(visibleProperty());
        return this;
    }

    public FxLabel tooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        tooltip.setShowDelay(Duration.millis(10));
        Tooltip.install(this, tooltip);
        getStyleClass().add("fx-label-with-tooltip");
        return this;
    }

//    public FxLabel tooltip(String tooltip) {
//        return tooltip(tooltip != null ? Fx.tooltip(tooltip) : null);
//    }

//    public FxLabel tooltip(String text, boolean install) {
//        if(install) {
//            tooltip(text);
//        } else {
//            uninstallTooltip();
//        }
//        return this;
//    }
//
//    public FxLabel tooltip(StringProperty tooltip) {
//        return tooltip(tooltip != null ? Fx.tooltip(tooltip) : null);
//    }
//
//    public FxLabel tooltip(StringProperty prop, boolean install) {
//        if(install) {
//            tooltip(prop);
//        } else {
//            uninstallTooltip();
//        }
//        return this;
//    }

    public FxLabel uninstallTooltip() {
        if(this.tooltip != null) {
            getStyleClass().remove("fx-label-with-tooltip");
            Tooltip.uninstall(this, tooltip);
        }
        return this;
    }

    public FxLabel debug(String color) {
        setStyle("-fx-border-width: 1px;-fx-border-color: "+color);
        return this;
    }

    public FxLabel debug() {
        return debug("red");
    }

    @Override
    public void dispose() {
        listeners.remove();
        tooltip = null;
    }
}