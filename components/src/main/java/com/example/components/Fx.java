package com.example.components;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.util.Duration;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class Fx {
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
}
