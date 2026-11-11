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

import java.util.List;

public class DriverStatsWindow {
    private final Stage stage;
    private final Label posLabel;
    private final Label lapLabel;
    private final Label lapsToGoLabel;
    private final Label curTimeLabel;
    private final Label gapLabel;
    private final Label lastLapLabel;

    public DriverStatsWindow() {
        stage = new Stage();

        Label statsTitle = createLabel("DRIVER STATS", 12, Color.GOLD);
        posLabel = createLabel("POS: P--", 16, Color.WHITE);
        lapLabel = createLabel("LAP: --", 13, Color.CYAN);
        lapsToGoLabel = createLabel("LAPS LEFT: --", 12, Color.YELLOW);
        curTimeLabel = createLabel("CUR: --:--.---", 11, Color.LIGHTBLUE);
        gapLabel = createLabel("GAP: --:--.---", 11, Color.LIGHTGREEN);
        lastLapLabel = createLabel("LAST: --:--.---", 11, Color.LIGHTGRAY);

        VBox root = new VBox(4, statsTitle, posLabel, lapLabel, lapsToGoLabel, curTimeLabel, gapLabel, lastLapLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 15, 10, 15));
        root.setStyle("-fx-background-color: rgba(15, 15, 15, 0.85); -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 10;");

        Scene scene = new Scene(root, 180, 165);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);

        stage.setX(260);
        stage.setY(600);

        makeDraggable(root, stage);
        stage.show();
    }

    public void updateStats(List<DriverStats> participants) {
        if (participants == null || participants.isEmpty()) return;

        DriverStats p = participants.get(0);

        Platform.runLater(() -> {
            int pos = p.getPosition();
            posLabel.setText(pos > 0 ? "POS: P" + pos : "POS: P--");

            int lap = p.getCurrentLap();
            lapLabel.setText(lap > 0 ? "LAP: " + lap : "LAP: --");

            int toGo = p.getLapsRemaining();
            if (toGo > 0) {
                lapsToGoLabel.setText(String.format("LAPS LEFT: %d", toGo));
            } else {
                lapsToGoLabel.setText("LAPS LEFT: --");
            }

            float cur = p.getCurrentTime();
            if (cur > 0.0f) {
                curTimeLabel.setText("CUR: " + DriverStats.formatTime(cur));
            } else {
                curTimeLabel.setText("CUR: --:--.---");
            }

            float gap = p.getGapAhead();
            if (gap > 0.0f) {
                gapLabel.setText("GAP: " + DriverStats.formatTime(gap));
                gapLabel.setTextFill(Color.LIGHTGREEN);
            } else {
                gapLabel.setText("GAP: ---");
                gapLabel.setTextFill(Color.GRAY);
            }

            float last = p.getLastLapTime();
            if (last > 0.0f) {
                lastLapLabel.setText("LAST: " + DriverStats.formatTime(last));
            } else {
                lastLapLabel.setText("LAST: --:--.---");
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
