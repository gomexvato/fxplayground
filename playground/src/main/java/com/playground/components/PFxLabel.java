package com.playground.components;

import javafx.scene.control.Label;
import com.playground.utils.ObservableListUtils;

public class PFxLabel extends Label {
    private static final String DISABLED_CLASS = "fx-disabled";

    public enum Emphasis {NONE, BOLD, RED, LIGHT_RED}

    public PFxLabel(String text, String style, Emphasis emphasis) {
        super(text);
        getStyleClass().add("fx-label");
        if (emphasis.equals(Emphasis.BOLD)) getStyleClass().add("fx-label-bold");
        if (emphasis.equals(Emphasis.RED)) getStyleClass().add("fx-label-red");
        if (emphasis.equals(Emphasis.LIGHT_RED)) getStyleClass().add("fx-label-lightred");
        if (style != null) getStyleClass().add(style);
        disabledProperty().addListener((o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), DISABLED_CLASS, nv));
    }

    public PFxLabel(String text, String style) {
        this(text, style, Emphasis.NONE);
    }

    public PFxLabel(String text) {
        this(text, null);
    }

    public static PFxLabel h1(String text) { return new PFxLabel(text, "fx-label-h1"); }
    public static PFxLabel h2(String text) { return new PFxLabel(text, "fx-label-h2"); }
    public static PFxLabel h3(String text) { return new PFxLabel(text, "fx-label-h3"); }
    public static PFxLabel h4(String text) { return new PFxLabel(text, "fx-label-h4"); }
    public static PFxLabel p(String text) { return new PFxLabel(text, "fx-label-p"); }
    public static PFxLabel p(String text, Emphasis emphasis) {
        return new PFxLabel(text, "fx-label-p", emphasis);
    }
    public static PFxLabel red(String text) { return new PFxLabel(text, null, Emphasis.RED); }
    public static PFxLabel lightRed(String text) { return new PFxLabel(text, null, Emphasis.LIGHT_RED); }
    public static PFxLabel bold(String text) {
        return new PFxLabel(text, null, Emphasis.BOLD);
    }

    public static PFxLabel wrapped(String text, int maxWidth) {
        PFxLabel label = PFxLabel.p(text);
        label.maxWidthProperty().set(maxWidth);
        label.wrapTextProperty().set(true);
        return label;
    }

    public PFxLabel addStyleClass(String clazz) {
        getStyleClass().add(clazz);
        return this;
    }

    public PFxLabel style(String style) {
        setStyle(style);
        return this;
    }

    public void setAllWidths(int width) {
        setMinWidth(width);
        setPrefWidth(width);
        setMaxWidth(width);
    }
}