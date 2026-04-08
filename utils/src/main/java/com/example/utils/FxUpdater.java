package com.example.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Convenient class to atomically keep track
 * of updates for a given function.
 */
public class FxUpdater {
    private final AtomicBoolean updating = new AtomicBoolean(false);

    public final Consumer<Runnable> updateFn = (Runnable r) -> {
        updating.set(true);
        r.run();
        updating.set(false);
    };

    public boolean isUpdating() {
        return updating.get();
    }
}
