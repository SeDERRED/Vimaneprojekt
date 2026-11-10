package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class DriverStatsWindow {
    private final Stage stage;
    private final VBox root;
    private final Label posLabel;
    private final Label currentLapLabel;
    private final Label lastLapLabel;
    private final double[] offset = new double[2];

    public DriverStatsWindow() {
        stage = new Stage();

        root = new VBox(6);
        root.setPadding(new Insets(12, 15, 12, 15));
        root.setStyle("-fx-background-color: rgba(15, 15, 15, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");

        Label title = new Label("DRIVER STATS");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        title.setTextFill(Color.GOLD);

        posLabel = createStatLabel("POS: --", 20, Color.WHITE);
        currentLapLabel = createStatLabel("CURRENT: --:--.---", 12, Color.CYAN);
        lastLapLabel = createStatLabel("LAST: --:--.---", 12, Color.LIGHTGREEN);

        root.getChildren().addAll(title, posLabel, currentLapLabel, lastLapLabel);

        root.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });

        Scene scene = new Scene(root, 220, 140);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setX(250);
        stage.setY(600);
        stage.setTitle("Driver Stats HUD");
        stage.show();
    }

    public void updateStats(List<Participant> participants) {
        if (participants == null || participants.isEmpty()) return;

        Participant p = participants.get(0);

        Platform.runLater(() -> {
            posLabel.setText("POS: P" + p.getPosition());
            currentLapLabel.setText("CURRENT: " + formatTime(p.getCurrentTime()));
            lastLapLabel.setText("LAST: " + formatTime(p.getLastLapTime()));
        });
    }

    private String formatTime(float timeInSeconds) {
        if (timeInSeconds <= 0 || Float.isNaN(timeInSeconds)) {
            return "--:--.---";
        }
        int minutes = (int) (timeInSeconds / 60);
        float seconds = timeInSeconds % 60;
        return String.format("%d:%06.3f", minutes, seconds);
    }

    private Label createStatLabel(String text, int fontSize, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize));
        label.setTextFill(color);
        return label;
    }
}