package com.playground.utils;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class PlaygroundUtils {

    public static long random(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void transition(Runnable r, Duration duration) {
        PauseTransition pt = new PauseTransition(duration);
        pt.setOnFinished(e -> r.run());
        pt.play();
    }

    public static void oneSecTransition(Runnable r) {
        transition(r, Duration.seconds(1));
    }
}
