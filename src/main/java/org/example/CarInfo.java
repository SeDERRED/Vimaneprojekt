package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CarInfo {

    private final Stage stage;
    private final Label fuelLabel;
    private final Label fuelPerLapLabel;
    private final Label damageLabel;

    private final Label[] wearLabels = new Label[4];
    private final Label[] tempLabels = new Label[4];
    private final Label[] pressLabels = new Label[4];

    private final double[] offset = new double[2];

    public CarInfo() {
        stage = new Stage();

        Label title = new Label("CAR INFO");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        title.setTextFill(Color.GOLD);

        fuelLabel = createLabel("FUEL: 0.0 L", 12, Color.WHITE);
        fuelPerLapLabel = createLabel("LAST LAP: -- L", 12, Color.LIGHTGRAY);
        damageLabel = createLabel("DAMAGE: 0%", 12, Color.LIME);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        String[] wheelNames = {"FL", "FR", "RL", "RR"};
        for (int i = 0; i < 4; i++) {
            wearLabels[i] = createLabel("100%", 11, Color.LIME);
            tempLabels[i] = createLabel("90°C", 11, Color.CYAN);
            pressLabels[i] = createLabel("28.0 PSI", 10, Color.GRAY);

            VBox box = new VBox(2,
                    createLabel(wheelNames[i], 10, Color.GOLD),
                    wearLabels[i], tempLabels[i], pressLabels[i]
            );
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 6; -fx-background-radius: 6;");
            grid.add(box, i % 2, i / 2);
        }

        VBox root = new VBox(8, title, fuelLabel, fuelPerLapLabel, grid, damageLabel);
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: rgba(15, 15, 15, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");

        // Логика перемещения окна мышкой
        root.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });

        Scene scene = new Scene(root, 220, 270);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setX(500);
        stage.setY(600);
        stage.setTitle("Car Info");
        stage.show();
    }

    public void updateInfo(float fuel, float fuelLastLap, float[] wear, float[] temp, float[] press, int damage) {
        Platform.runLater(() -> {
            fuelLabel.setText(String.format("FUEL: %.1f L", fuel));
            if (fuelLastLap > 0) {
                fuelPerLapLabel.setText(String.format("LAST LAP: %.2f L", fuelLastLap));
            }

            if (wear != null && temp != null && press != null) {
                for (int i = 0; i < 4; i++) {
                    wearLabels[i].setText(String.format("%.0f%%", wear[i]));
                    tempLabels[i].setText(String.format("%.0f°C", temp[i]));
                    pressLabels[i].setText(String.format("%.1f PSI", press[i]));

                    if (wear[i] < 50) wearLabels[i].setTextFill(Color.RED);
                    else if (wear[i] < 75) wearLabels[i].setTextFill(Color.YELLOW);
                    else wearLabels[i].setTextFill(Color.LIME);
                }
            }

            damageLabel.setText("DAMAGE: " + damage + "%");
            if (damage > 50) damageLabel.setTextFill(Color.RED);
            else if (damage > 0) damageLabel.setTextFill(Color.YELLOW);
            else damageLabel.setTextFill(Color.LIME);
        });
    }

    private Label createLabel(String text, int fontSize, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize));
        label.setTextFill(color);
        return label;
    }
}