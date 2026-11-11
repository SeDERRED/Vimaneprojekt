package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CarInfoWindow {
    private final Stage stage;
    private final Label fuelLabel;
    private final Label wearLabel;


    public CarInfoWindow() {
        stage = new Stage();

        Label title = createLabel("CAR STATUS", 12, Color.GOLD);
        fuelLabel = createLabel("FUEL: -- L", 11, Color.WHITE);
        wearLabel = createLabel("TYRES: --%", 11, Color.LIGHTGRAY);

        VBox root = new VBox(4, title, fuelLabel, wearLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 15, 10, 15));
        root.setStyle("-fx-background-color: rgba(15, 15, 15, 0.85); -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 10;");

        Scene scene = new Scene(root, 180, 110);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);

        // Размещаем окно дальше по экрану
        stage.setX(470);
        stage.setY(600);

        makeDraggable(root, stage);
        stage.show();
    }

    public void updateInfo(float fuel, float fuelLastLap, float[] wear, float[] temp, float[] press, int damage) {
        Platform.runLater(() -> {
            fuelLabel.setText(String.format("FUEL: %.1f L", fuel));
            if (wear != null && wear.length > 0) {
                wearLabel.setText(String.format("TYRE FL: %.0f%%", wear[0]));
            }
        });
    }

    private Label createLabel(String text, int fontSize, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize));
        label.setTextFill(color);
        return label;
    }

    private void makeDraggable(VBox node, Stage stage) {
        final double[] offset = new double[2];
        node.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });
        node.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });
    }
}