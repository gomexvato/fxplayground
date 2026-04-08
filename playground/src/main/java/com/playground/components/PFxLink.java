package com.playground.components;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import com.playground.utils.ObservableListUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.function.Supplier;

public class PFxLink extends Hyperlink {
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty();
    private final String CLASS = "fx-link";
    private final String SELECTED_CLASS = "fx-link-selected";
    public final String initialText;
    public final String selectedText;

    public PFxLink(String initialText, String selectedText, Runnable r) {
        this.initialText = initialText;
        this.selectedText = selectedText;
        getStyleClass().add(CLASS);
        setText(initialText);
        setOnAction(actionEvent -> {
            selectedProperty.setValue(!selectedProperty.getValue());
            if(r!=null) r.run();
        });
        if(!initialText.equals(selectedText)) {
            textProperty().bind(PFxBinds.when(selectedProperty(), selectedText, initialText));
        }
        selectedProperty.addListener(
            (o, ov, nv) -> ObservableListUtils.addOrRemove(getStyleClass(), SELECTED_CLASS, nv));
    }

    public PFxLink(String initialText, String selectedText) {
        this(initialText, selectedText, null);
    }

    public PFxLink(String text, Runnable runnable) {
        this(text, text, runnable);
    }

    public PFxLink(Image image, String text, Runnable runnable) {
        this(text, runnable);
        ImageView view = new ImageView();
        view.setImage(image);
        view.setFitHeight(13);
        view.setFitWidth(13);
        setGraphic(view);
    }

    public PFxLink(String text) {
        this(text, text, null);
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public static PFxLink highlightedLink(String text, Runnable runnable) {
        PFxLink link = new PFxLink(text, runnable);
        link.getStyleClass().add("fx-link-highlight");
        return link;
    }

    public static PFxLink highlightedLink(String initialText, String selectedText, Runnable runnable) {
        PFxLink link = new PFxLink(initialText, selectedText, runnable);
        link.getStyleClass().add("fx-link-highlight");
        return link;
    }

    public static PFxLink highlightedLink(String initialText, String selectedText) {
        PFxLink link = new PFxLink(initialText, selectedText);
        link.getStyleClass().add("fx-link-highlight");
        return link;
    }

    public static PFxLink copyToClipboard(Supplier<String> stringSupplier) {
        String copyToClipBoardStr = "Copy to Clipboard";
        PFxLink link = PFxLink.highlightedLink(copyToClipBoardStr, () -> {
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

    public static PFxLink hamburger() {
        PFxLink link = new PFxLink("");
        link.getStyleClass().add("fx-hamburger");
        return link;
    }
}