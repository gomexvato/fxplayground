package com.utils;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.function.Function;

public class FxBindings {

    private static final Function<ObjectProperty<Boolean>[], BooleanBinding> bindAnyFn = a -> {
        BooleanBinding binding = new SimpleBooleanProperty(true).not();
        for (ObjectProperty<Boolean> p : a) binding = binding.or(p.isEqualTo(true));
        return binding;
    };

    private static final Function<ObjectProperty<Boolean>[], BooleanBinding> bindAllFn = a -> {
        BooleanBinding binding = new SimpleBooleanProperty(false).not();
        for (ObjectProperty<Boolean> p : a) binding = binding.and(p.isEqualTo(true));
        return binding;
    };

    // Provides a boolean property to track when any of the properties in the array becomes true
    public static BooleanProperty bindToAny(ObjectProperty<Boolean>[] arr) {
        return bindToFn(arr, bindAnyFn);
    }

    // Provides a boolean property to track when all the properties in the array becomes true
    public static BooleanProperty bindToAll(ObjectProperty<Boolean>[] arr) {
        return bindToFn(arr, bindAllFn);
    }

    private static BooleanProperty bindToFn(
        ObjectProperty<Boolean>[] arr,
        Function<ObjectProperty<Boolean>[], BooleanBinding> fn
    ) {
        BooleanProperty prop = new SimpleBooleanProperty(fn.apply(arr).get());
        for(ObjectProperty<Boolean> p: arr) p.addListener((o, ov, nv) -> prop.set(fn.apply(arr).get()));
        return prop;
    }
}
