package org.example;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {

        new Thread(() -> Application.launch(HudApplication.class, args)).start();


        UdpListener listener = new UdpListener(telemetryPoint -> {
            if (HudApplication.getInstance() != null) {
                HudApplication.getInstance().updateTelemetry(telemetryPoint);
            }
        });

        listener.start();
    }
}