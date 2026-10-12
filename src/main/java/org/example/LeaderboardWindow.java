package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class LeaderboardWindow {
    private final Stage stage;
    private final VBox rowsContainer;
    private final double[] offset = new double[2];

    public LeaderboardWindow() {
        stage = new Stage();

        rowsContainer = new VBox(4);
        rowsContainer.setPadding(new Insets(10, 15, 10, 15));
        rowsContainer.setStyle("-fx-background-color: rgba(15, 15, 15, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");

        // Заголовок таблицы
        Label title = new Label("LEADERBOARD");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        title.setTextFill(Color.GOLD);
        rowsContainer.getChildren().add(title);

        // --- ЛОГИКА ПЕРЕТАСКИВАНИЯ МЫШКОЙ ---
        rowsContainer.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });

        rowsContainer.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });

        Scene scene = new Scene(rowsContainer, 280, 220);
        scene.setFill(Color.TRANSPARENT);

        // Настройки прозрачного HUD-оверлея
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setX(250);
        stage.setY(600);
        stage.setTitle("AMS2 Leaderboard HUD");
        stage.show();
    }
    public void updateLeaderboard(List<Participant> participants) {
        Platform.runLater(() -> {

            if (rowsContainer.getChildren().size() > 1) {
                rowsContainer.getChildren().subList(1, rowsContainer.getChildren().size()).clear();
            }

            for (Participant p : participants) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);


                row.setMouseTransparent(true);

                Label pos = new Label("P" + p.getPosition());
                pos.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
                pos.setTextFill(Color.WHITE);
                pos.setPrefWidth(30);

                Label name = new Label(p.getName());
                name.setFont(Font.font("Consolas", 12));
                name.setTextFill(Color.LIGHTGRAY);
                name.setPrefWidth(90);


                float lapTimeSec = p.getLastLapTime();
                String lapTimeStr = lapTimeSec > 0 ? String.format("%.1fs", lapTimeSec) : "--";
                Label lapTime = new Label(lapTimeStr);
                lapTime.setFont(Font.font("Consolas", 11));
                lapTime.setTextFill(Color.CYAN);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);


                Label pit = new Label(p.isInPits() ? "PIT" : "");
                pit.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
                pit.setTextFill(Color.RED);

                row.getChildren().addAll(pos, name, lapTime, spacer, pit);
                rowsContainer.getChildren().add(row);
            }
        });
    }
}

