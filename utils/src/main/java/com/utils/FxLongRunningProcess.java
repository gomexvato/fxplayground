package com.utils;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

// This allows us to disable components while we wait for slow operations.
// Prevents the UI from waiting forever.
public class FxLongRunningProcess {
    private final Duration maxDuration;
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);
    private PauseTransition pauseTransition;

    public FxLongRunningProcess(Duration maxDuration) {
        this.maxDuration = maxDuration;
    }

    public static PauseTransition pauseBeforeRun(Duration duration, Runnable r) {
        PauseTransition pause = new PauseTransition(duration);
        pause.setOnFinished(e -> r.run());
        pause.play();
        return pause;
    }

    public synchronized void start() {
        runningProperty.set(true);
        pauseTransition = pauseBeforeRun(maxDuration, this::abort);
    }

    public synchronized void done() {
        if(pauseTransition != null) { // Done before max duration
            pauseTransition.stop();
            pauseTransition = null;
            runningProperty.set(false);
        }
    }

    private void abort() {
        runningProperty.set(false);
        pauseTransition = null;
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return runningProperty;
    }
}