package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private int clickCount = 0;
    private Label countLabel;

    @Override
    public void start(Stage primaryStage) {
        countLabel = new Label("Clicks: 0");
        countLabel.setId("countLabel");
        countLabel.setStyle("-fx-font-size: 24px;");

        Button clickButton = new Button("Click me!");
        clickButton.setId("clickButton");
        clickButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        clickButton.setOnAction(e -> {
            clickCount++;
            countLabel.setText("Clicks: " + clickCount);
        });

        Button resetButton = new Button("Reset");
        resetButton.setId("resetButton");
        resetButton.setStyle("-fx-font-size: 14px;");
        resetButton.setOnAction(e -> {
            clickCount = 0;
            countLabel.setText("Clicks: 0");
        });

        VBox root = new VBox(20, countLabel, clickButton, resetButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("FX Playground");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
