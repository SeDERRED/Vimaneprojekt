package org.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpListener {
    private static final int PORT=5606;
    private static final int BUFFER_SIZE =2048;
    private volatile boolean running = false;

    public void start(){
        running = true;

        Thread listenerThread = new Thread(this::listen);
        listenerThread.setDaemon(true); // Поток закроется сам при завершении программы
        listenerThread.start();
        System.out.println("UDP proslushka po"+PORT);
    }
    private void listen() {
        byte[] buffer = new byte[BUFFER_SIZE];

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Ожидаем пакет от игры

                // На время теста просто выводим размер принятого пакета
                System.out.println("[UDP Listener] Получен пакет размером: " + packet.getLength() + " байт");
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
