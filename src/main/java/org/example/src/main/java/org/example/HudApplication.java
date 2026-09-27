package org.example;

import javafx.application.Application;
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


public class HudApplication extends Application {
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


        gearLabel = createLabel("N", 48, Color.GOLD);
        speedLabel = createLabel("0 KM/H", 24, Color.WHITE);
        rpmLabel = createLabel("0 RPM", 16, Color.LIGHTGRAY);


        VBox root = new VBox(4, gearLabel, speedLabel, rpmLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));


        root.setStyle("-fx-background-color: rgba(15, 15, 15, 0.8); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");


        Scene scene = new Scene(root, 180, 140);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);


        primaryStage.setX(50);
        primaryStage.setY(600);


        makeDraggable(root, primaryStage);

        primaryStage.show();
    }


    public void updateTelemetry(Telemetry point) {
        Platform.runLater(() -> {
            speedLabel.setText((int) point.speedKmh() + " KM/H");
            rpmLabel.setText(point.rpm() + " RPM");

            int gear = point.gear();

            if (gear == 15) {
                gearLabel.setText("R");
            } else if (gear == 0) {
                gearLabel.setText("N");
            } else if (gear >= 1) {
                gearLabel.setText(String.valueOf(gear - 1 + 1));
            } else {
                gearLabel.setText("-");
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
