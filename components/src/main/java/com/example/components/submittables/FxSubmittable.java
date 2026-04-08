package com.example.components.submittables;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Interface for Submittable components
 * @param <T> Submittable value
 */
public interface FxSubmittable<T> {
    //Consumer of user selections
//    FxSubmittable<T> consume(Consumer<T> consumer);

    // Controls to update the UI
//    FxSubmittable<T> set(T t);
    void setDouble(Double value); // ASPEN factor value
}