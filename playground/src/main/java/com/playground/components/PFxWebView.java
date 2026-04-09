package com.playground.components;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PFxWebView {
    public final WebView view;

    public PFxWebView(String html, String script) {
        view = new WebView();
        WebEngine engine = view.getEngine();
        engine.setJavaScriptEnabled(true);
        if(script != null) {
            engine.getLoadWorker().stateProperty().addListener((o, ov, nv) -> {
                if (nv == Worker.State.SUCCEEDED) {
                    engine.executeScript(script);
                }
            });
        }
        engine.loadContent(html);
        view.requestFocus();
    }

    public PFxWebView(String html) {
        this(html, null);
    }
}
