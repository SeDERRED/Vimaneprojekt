package org.example;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // 1. Запуск HUD оверлея
        new Thread(() -> Application.launch(HudApplication.class)).start();

        // 2. Запуск сетевого слушателя UDP
        UdpListener listener = new UdpListener(telemetryPoint -> {
            if (HudApplication.getInstance() != null) {
                HudApplication.getInstance().updateTelemetry(telemetryPoint);
            }
        });

        listener.start();
    }
}