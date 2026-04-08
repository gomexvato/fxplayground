package com.components.buttons;


import com.components.FxBinds;
import com.utils.ObservableListUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.function.Supplier;

public class FxLink extends Hyperlink {
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty();
    private final String CLASS = "fx-link";
    private final String SELECTED_CLASS = "fx-link-selected";
    public final String initialText;
    public final String selectedText;

    public FxLink(String initialText, String selectedText, Runnable r) {
        this.initialText = initialText;
        this.selectedText = selectedText;
        getStyleClass().add(CLASS);
        setText(initialText);
        setOnAction(actionEvent -> {
            selectedProperty.setValue(!selectedProperty.getValue());
            if(r!=null) r.run();
        });
        if(!initialText.equals(selectedText)) {
            textProperty().bind(FxBinds.when(selectedProperty(), selectedText, initialText));
        }
        selectedProperty.addListener((o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), SELECTED_CLASS, nv));
    }

    public FxLink(String initialText, String selectedText) {
        this(initialText, selectedText, null);
    }

    public FxLink(String text, Runnable runnable) {
        this(text, text, runnable);
    }

    public FxLink(Image image, String text, Runnable runnable) {
        this(text, runnable);
        ImageView view = new ImageView();
        view.setImage(image);
        view.setFitHeight(13);
        view.setFitWidth(13);
        setGraphic(view);
    }

    public FxLink(String text) {
        this(text, text, null);
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public void toggle() {
        fire();
    }

    public static FxLink highlightedLink(String text, Runnable runnable) {
        FxLink link = new FxLink(text, runnable);
        link.getStyleClass().add("fx-link-highlight");
        return link;
    }

    public static FxLink highlightedLink(String initialText, String selectedText, Runnable runnable) {
        FxLink link = new FxLink(initialText, selectedText, runnable);
        link.getStyleClass().add("fx-link-highlight");
        return link;
    }

    public static FxLink highlightedLink(String initialText, String selectedText) {
        FxLink link = new FxLink(initialText, selectedText);
        link.getStyleClass().add("fx-link-highlight");
        return link;
    }

    public static FxLink copyToClipboard(Supplier<String> stringSupplier) {
        String copyToClipBoardStr = "Copy to Clipboard";
        FxLink link = FxLink.highlightedLink(copyToClipBoardStr, () -> {
            StringSelection selection = new StringSelection(stringSupplier.get());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });
        link.selectedProperty.addListener((o) -> {
            link.setText("Copied!");
            PauseTransition pt = new PauseTransition(Duration.seconds(1));
            pt.setOnFinished(e -> link.setText(copyToClipBoardStr));
            pt.play();
        });
        return link;
    }

    public static FxLink hamburger() {
        FxLink link = new FxLink("");
        link.getStyleClass().add("fx-hamburger");
        return link;
    }

    public FxLink style(String value) {
        setStyle(value);
        return this;
    }
}