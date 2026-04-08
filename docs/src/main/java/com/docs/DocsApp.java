package com.docs;

import com.playground.Playground;

public class DocsApp extends Playground {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String[] stylesheets() {
        return new String[]{
                "styles/playground.css",
        };
    }
}