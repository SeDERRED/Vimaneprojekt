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
        System.out.println("Прослушка по " + PORT);
    }

    private void listen() {
        byte[] buffer = new byte[BUFFER_SIZE];

        try (DatagramSocket socket = new DatagramSocket(PORT)) {

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                int len = packet.getLength();
                if (len < 12) continue;

                byte[] packetData = new byte[len];
                System.arraycopy(buffer, 0, packetData, 0, len);

                int packetType = packetData[10] & 0xFF;

                TelemetryParser.parseCarInfoPacket(packetData);
                TelemetryParser.parseParticipantsNamesPacket(packetData);

                // 2. Основная телеметрия (тип 0: спидометр, обороты, передача)
                if (packetType == TelemetryParser.PACKET_TYPE_TELEMETRY) {
                    Telemetry telemetryPoint = TelemetryParser.parseTelemetryPacket(packetData);
                    if (onTelemetryReceived != null && telemetryPoint != null) {
                        onTelemetryReceived.accept(telemetryPoint);
                    }

                    TelemetryParser.parseCarInfoPacket(packetData);
                }

                // 3. Тайминги (тип 3 или 7: круги, позиция, времена)
                if (packetType == TelemetryParser.PACKET_TYPE_TIMINGS_3 || packetType == TelemetryParser.PACKET_TYPE_TIMINGS_7) {

                    List<DriverStats> driverStatsList = TelemetryParser.parseDriverStatsPacket(packetData);
                    if (driverStatsList != null && !driverStatsList.isEmpty()) {
                        if (HudApplication.getInstance() != null) {
                            HudApplication.getInstance().updateDriverStats(driverStatsList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("[UDP ошибка]: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
    }
}
