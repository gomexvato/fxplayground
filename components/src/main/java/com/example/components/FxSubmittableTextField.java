package com.example.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Consumer;

public class FxSubmittableTextField extends HBox {

    private final TextField textField = new TextField();
    private final Button submitButton = new Button("Submit");
    private final ObjectProperty<Consumer<String>> onSubmit = new SimpleObjectProperty<>();

    public FxSubmittableTextField() {
        this(null);
    }

    public FxSubmittableTextField(String promptText) {
        super(8);

        if (promptText != null) {
            textField.setPromptText(promptText);
        }

        textField.setId("submittableTextField");
        submitButton.setId("submitButton");

        HBox.setHgrow(textField, Priority.ALWAYS);

        submitButton.setOnAction(e -> handleSubmit());
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSubmit();
            }
        });

        getChildren().addAll(textField, submitButton);
    }

    private void handleSubmit() {
        Consumer<String> handler = onSubmit.get();
        if (handler != null) {
            handler.accept(textField.getText());
        }
        textField.clear();
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }

    public String getText() {
        return textField.getText();
    }

    public ObjectProperty<Consumer<String>> onSubmitProperty() {
        return onSubmit;
    }

    public void setOnSubmit(Consumer<String> handler) {
        onSubmit.set(handler);
    }

    public Consumer<String> getOnSubmit() {
        return onSubmit.get();
    }
}
