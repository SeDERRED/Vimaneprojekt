package org.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.function.Consumer;

public class UdpListener {
    private static final int PORT = 5606;
    private static final int BUFFER_SIZE = 2048;
    private volatile boolean running = false;

    private final Consumer<Telemetry> onTelemetryReceived;

    public UdpListener(Consumer<Telemetry> onTelemetryReceived) {
        this.onTelemetryReceived = onTelemetryReceived;
    }

    public void start() {
        running = true;

        Thread listenerThread = new Thread(this::listen);
        listenerThread.setDaemon(true);
        listenerThread.start();
        System.out.println("UDP прослушка запущенна на порту " + PORT);
    }

    private void listen() {
        byte[] buffer = new byte[BUFFER_SIZE];

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                byte[] packetData = packet.getData();

                TelemetryParser.parseParticipantsNamesPacket(packetData);

                // 2. Обработка основной телеметрии для спидометра (Packet Type 0)
                Telemetry telemetryPoint = TelemetryParser.parseTelemetryPacket(packetData);
                if (onTelemetryReceived != null && telemetryPoint != null) {
                    onTelemetryReceived.accept(telemetryPoint);
                }


                TelemetryParser.parseCarInfoPacket(packetData);


                List<Participant> participantsList = TelemetryParser.parseParticipantsPacket(packetData);
                if (participantsList != null && !participantsList.isEmpty()) {
                    if (HudApplication.getInstance() != null && HudApplication.getInstance().getLeaderboardWindow() != null) {
                        HudApplication.getInstance().getLeaderboardWindow().updateLeaderboard(participantsList);
                    }
                }
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("[UDP Listener] Ошибка при приеме пакета: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        System.out.println("[UDP Listener] Сервер остановлен.");
    }
}