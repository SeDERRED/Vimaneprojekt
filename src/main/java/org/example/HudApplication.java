package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.TelemetryPoint;

public class HudApplication {
    private Label speedLabel;
    private Label gearLabel;
    private Label rpmLabel;

    private static HudApplication instance;

    public static HudApplication getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        // --- Элементы интерфейса ---
        gearLabel = createLabel("N", 48, Color.GOLD);
        speedLabel = createLabel("0 KM/H", 24, Color.WHITE);
        rpmLabel = createLabel("0 RPM", 16, Color.LIGHTGRAY);

        // Контейнер для показателей
        VBox root = new VBox(4, gearLabel, speedLabel, rpmLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));

        // Полупрозрачный темный фон для отличной читаемости на любой трассе
        root.setStyle("-fx-background-color: rgba(15, 15, 15, 0.8); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");

        // Создаем сцену с прозрачным фоном
        Scene scene = new Scene(root, 180, 140);
        scene.setFill(Color.TRANSPARENT);

        // Настройки прозрачного окна поверх игры
        primaryStage.initStyle(StageStyle.TRANSPARENT); // Убираем стандартную рамку Windows
        primaryStage.setAlwaysOnTop(true);             // Окно всегда поверх остальных
        primaryStage.setScene(scene);

        // Начальная позиция HUD на экране (X: 50, Y: 600)
        primaryStage.setX(50);
        primaryStage.setY(600);

        // Включаем возможность перетаскивать HUD мышкой
        makeDraggable(root, primaryStage);

        primaryStage.show();
    }

    // Метод для безопасного обновления HUD из сетевого потока UDP
    public void updateTelemetry(TelemetryPoint point) {
        Platform.runLater(() -> {
            speedLabel.setText((int) point.speedKmh() + " KM/H");
            rpmLabel.setText(point.rpm() + " RPM");

            // Логика передачи (в структуре PCars/AMS2: 0=Задняя, 1=Нейтраль, 2=Первая...)
            int gear = point.gear();
            if (gear == 0) {
                gearLabel.setText("R");
            } else if (gear == 1) {
                gearLabel.setText("N");
            } else {
                gearLabel.setText(String.valueOf(gear - 1));
            }
        });
    }

    private Label createLabel(String text, int fontSize, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize));
        label.setTextFill(color);
        return label;
    }

    // Перетаскивание окна мышки
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
