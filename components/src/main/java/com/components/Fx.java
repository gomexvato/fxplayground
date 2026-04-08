package com.components;

import com.components.boxes.FxHBox;
import com.components.boxes.FxVBox;
import com.components.buttons.FxLink;
import com.components.submittables.FxSubmittableTextField;
import com.core.Unit;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.function.*;

public class Fx {
    /**
     * Convenience methods to access FxVBox, FxHBox
     **/
    public static FxVBox vBox(int spacing, String style) { return new FxVBox(spacing, style);}
    public static FxVBox vBox(int spacing) {
        return new FxVBox(spacing);
    }
    public static FxVBox vBox(String style) { return new FxVBox(style); }
    public static FxVBox vBox(int spacing, Node... nodes) { return new FxVBox(spacing, nodes); }
    public static FxVBox vBox(String style, Node... nodes) { return new FxVBox(style, nodes); }
    public static FxVBox vBox(Node... nodes) { return new FxVBox(nodes); }
    public static FxVBox vBox(boolean separated, Node... nodes) {
        return new FxVBox(separated, nodes);
    }
    public static FxVBox vBox() {
        return new FxVBox();
    }
    public static FxVBox vBoxMD(Node... nodes) { return (new FxVBox(nodes)).addStyleClass("fx-pane-md"); }

    public static FxHBox hBox(int spacing, String style) { return new FxHBox(spacing, style);}
    public static FxHBox hBox(int spacing) {
        return new FxHBox(spacing);
    }
    public static FxHBox hBox(String style) { return new FxHBox(style); }
    public static FxHBox hBox(Node... nodes) { return new FxHBox(nodes); }
    public static FxHBox hBox(int spacing, Node... nodes) {
        FxHBox b = new FxHBox(nodes);
        b.setSpacing(spacing);
        return b;
    }
    public static FxHBox hBox() {
        return new FxHBox();
    }
    public static FxHBox row(String text, Node node) {
        return row(Fx.label(text), node);
    }

    public static FxHBox row(FxLabel label, Node node) {
        return Fx.hBox(label, FxHBox.spacer(), node).addStyleClass("fx-row");
    }
    /**
     * Convenience methods to access FxLabel
     **/
    public static Group vLabel(String text) {
        return new Group(FxLabel.vertical(text));
    }
    public static FxLabel label() { return FxLabel.p(""); }
    public static FxLabel h1(String text) { return FxLabel.h1(text); }
    public static FxLabel label(String text) { return FxLabel.p(text); }
    public static FxLabel label(StringProperty prop) { return FxLabel.p(prop); }
    public static FxLabel label(StringProperty prop, String clazz) { return FxLabel.p(prop, clazz); }
    public static <T> FxLabel label(ObjectProperty<T> prop) { return FxLabel.p(prop); }
    public static <T> FxLabel label(ReadOnlyObjectProperty<T> prop) { return FxLabel.p(prop); }
    public static <T> FxLabel label(ObjectProperty<T> prop, String clazz) { return FxLabel.p(prop, clazz); }
    public static <T> FxLabel label(ReadOnlyDoubleProperty prop, Function<Double, String> formatterFn) {
        FxLabel label = Fx.label(formatterFn.apply(prop.getValue()));
        prop.addListener((o, ov, nv) -> label.setText(formatterFn.apply(nv.doubleValue())));
        return label;
    }

    public static FxTooltip tooltip(String tooltip)  { return new FxTooltip(tooltip); }
    public static FxTooltip tooltip(StringProperty prop)  { return new FxTooltip(prop); }

    public static FxLink link(String text, Runnable r) { return new FxLink(text, r); }


    public static PauseTransition pauseBeforeRun(Duration duration, Runnable r) {
        PauseTransition pause = new PauseTransition(duration);
        pause.setOnFinished(e -> r.run());
        pause.play();
        return pause;
    }

    /**
     * Convenience methods to access JavaFX Properties
     **/
    public static StringProperty sProp() { return new SimpleStringProperty(); }
    public static StringProperty sProp(String value) { return new SimpleStringProperty(value); }
    public static BooleanProperty bProp() { return new SimpleBooleanProperty(); }
    public static BooleanProperty bProp(boolean value) { return new SimpleBooleanProperty(value); }
    public static IntegerProperty iProp() { return new SimpleIntegerProperty(); }
    public static IntegerProperty iProp(int value) { return new SimpleIntegerProperty(value); }
    public static DoubleProperty dProp() { return new SimpleDoubleProperty(); }
    public static DoubleProperty dProp(double value) { return new SimpleDoubleProperty(value); }
    public static <T> ObjectProperty<T> oProp() { return new SimpleObjectProperty<>(); }
    public static <T> ObjectProperty<T> oProp(T value) { return new SimpleObjectProperty<>(value); }
    public static IntegerProperty roiWrapper() { return new ReadOnlyIntegerWrapper(); }

    public static IntegerProperty[] iProps(Integer[] values) {
        return props(values, SimpleIntegerProperty[]::new, SimpleIntegerProperty::new);
    }

    public static IntegerProperty[] iProps(int values) {
        return props(values, SimpleIntegerProperty[]::new, SimpleIntegerProperty::new);
    }

    public static StringProperty[] sProps(String[] values) {
        return props(values, SimpleStringProperty[]::new, SimpleStringProperty::new);
    }

    public static StringProperty[] sProps(int values) {
        return props(values, SimpleStringProperty[]::new, SimpleStringProperty::new);
    }

    public static DoubleProperty[] dProps(Double... values) {
        return props(values, SimpleDoubleProperty[]::new, SimpleDoubleProperty::new);
    }

    public static DoubleProperty[] dProps(int values) {
        return props(values, SimpleDoubleProperty[]::new, SimpleDoubleProperty::new);
    }

    public static BooleanProperty[] bProps(int values) {
        return props(values, SimpleBooleanProperty[]::new, SimpleBooleanProperty::new);
    }

    public static <T> ObjectProperty<T>[] oProps(T... values) {
        return props(values, SimpleObjectProperty[]::new, SimpleObjectProperty::new);
    }

    public static <T> ObjectProperty<T>[] oProps(int values) {
        return props(values, SimpleObjectProperty[]::new, SimpleObjectProperty::new);
    }

    // Array of properties with default values (ie: new SimpleStringProperty("default-x"))
    private static <R, T extends R, Z> T[] props(
            Z[] values,
            IntFunction<R[]> tArrInstanceFn,
            Function<Z, T> tInstanceFn
    ) {
        T[] props = (T[]) tArrInstanceFn.apply(values.length);
        for(int i=0;i<values.length;i++) {
            props[i] = values[i] != null ? tInstanceFn.apply(values[i]) : null;
        }
        return props;
    }

    // Array of properties with no default (ie: new SimpleStringProperty());
    private static <R, T extends R, Z> T[] props(
            int length,
            Function<Integer, R[]> tArrInstanceFn,
            Supplier<T> tInstanceFn
    ) {
        T[] props = (T[]) tArrInstanceFn.apply(length);
        for(int i=0;i<length;i++) props[i] = tInstanceFn.get();
        return props;
    }

    /**
     * Convenient methods to instantiate submittable text field
     */

    public static <T> FxSubmittableTextField<T> textField() {
        return textField(true, null);
    }

    public static <T> FxSubmittableTextField<T> textField(BiConsumer<FxSubmittableTextField<T>, T> consumer) {
        return textField(true, consumer);
    }
    public static <T> FxSubmittableTextField<T> textField(boolean editable) {
        return new FxSubmittableTextField<>(null, null, editable, null);
    }
    public static <T> FxSubmittableTextField<T> textField(boolean editable,
                                                          BiConsumer<FxSubmittableTextField<T>, T> consumer) {
        return new FxSubmittableTextField<>(null, null, editable, consumer);
    }
    public static <T> FxSubmittableTextField<T> textField(Unit unit, String format) {
        return new FxSubmittableTextField<>(format, unit);
    }
    public static <T> FxSubmittableTextField<T> textField(Unit unit, String format,
                                                          Pair<Predicate<T>,String>... validations) {
        return new FxSubmittableTextField<>(format, unit, null, validations);
    }
    public static <T> FxSubmittableTextField<T> textField(Unit unit, String format,
                                                          BiConsumer<FxSubmittableTextField<T>, T> consumer,
                                                          Pair<Predicate<T>,String> validations
    ) {
        return new FxSubmittableTextField<>(format, unit, consumer, validations);
    }

    public static FxSubmittableTextField<Double> dTextField() {
        return textField(true, null);
    }

    public static FxSubmittableTextField<Double> dTextField(Unit unit, String format,
                                                            Pair<Predicate<Double>, String>... validations) {
        return new FxSubmittableTextField<>(format, unit, null, validations);
    }

    public static FxSubmittableTextField<Double> dTextField(Unit unit, String format,
                                                            Function<Double, String> isValidationFn) {
        return new FxSubmittableTextField<>(format, unit, null, isValidationFn);
    }
}
