package org.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TelemetryParser {

    private static final int PACKET_TYPE_TELEMETRY = 0;
    private static final int PACKET_TYPE_PARTICIPANTS = 3;
    private static final int PACKET_TYPE_TIMINGS = 7;

    private static float fuelAtLapStart =-1.0f;
    private static float lastCalculatedFuelPerLap = 0.0f;
    private static float lastDistance = 0.0f;
    private static final String[] driverNames = new String[32];

    public static Telemetry parseTelemetryPacket(byte[] buffer) {

        if (buffer == null || buffer.length < 500) {
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {

            int packetNumber = bb.getInt();
            int categoryPacketNumber = bb.getInt();
            byte partialPacketIndex = bb.get();
            byte partialPacketNumber = bb.get();
            byte packetType = bb.get();
            byte packetVersion = bb.get();


            if (packetType != PACKET_TYPE_TELEMETRY) {
                return null;
            }



            bb.position(36);
            float speedMps = bb.getFloat();


            if (Float.isNaN(speedMps) || Float.isInfinite(speedMps) || speedMps < 0 || speedMps > 200) {
                speedMps = 0.0f;
            }
            float speedKmh = speedMps * 3.6f;


            int rpm = bb.getShort() & 0xFFFF;
            if (rpm > 20000) rpm = 0; // Защита от мусорных данных


            bb.position(45);
            byte gearByte = bb.get();
            int gear = gearByte & 0x0F;


            bb.position(29);
            float brake = (bb.get() & 0xFF) / 255.0f;
            float throttle = (bb.get() & 0xFF) / 255.0f;

            bb.position(88);
            float distanceMeters = bb.getFloat();
            if (Float.isNaN(distanceMeters) || distanceMeters < 0) {
                distanceMeters = 0.0f;
            }
            bb.position(60);
            int racePosition = bb.get() & 0xFF;

            return new Telemetry(
                    distanceMeters,
                    speedKmh,
                    throttle,
                    brake,
                    gear,
                    rpm
            );


        } catch (Exception e) {
            return null;
        }
    }
    public static List<Participant> parseParticipantsPacket(byte[] buffer) {
        if (buffer == null || buffer.length < 15) {
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {
            // Байт типа пакета со смещения 10
            bb.position(10);
            byte packetType = bb.get();

            if (packetType != PACKET_TYPE_TIMINGS) {
                return null;
            }

            List<Participant> participants = new ArrayList<>();

            // Смещение 12 — количество участников в сессии
            bb.position(12);
            byte numParticipants = bb.get();

            // Смещение 15 — начало массива структур участников sParticipants (до 32 участников)
            bb.position(15);

            for (int i = 0; i < Math.min(numParticipants, 32); i++) {
                short currentLapDistance = bb.getShort();

                byte racePosAndFlags = bb.get();
                int position = racePosAndFlags & 0x7F;

                byte currentLap = bb.get();
                float currentTime = bb.getFloat();
                float currentSectorTime = bb.getFloat();
                float lastLapTime = bb.getFloat();

                byte pitModeAndSchedule = bb.get();
                int pitMode = pitModeAndSchedule & 0x0F;
                boolean inPits = pitMode > 0;

                bb.position(bb.position() + 8);

                if (position > 0) {
                    String driverName = "Driver " + (i + 1);
                    float gapToAhead = 0.0f;

                    participants.add(new Participant(
                            driverName,
                            position,
                            gapToAhead,
                            lastLapTime,
                            inPits
                    ));
                }
            }


            participants.sort((p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()));

            return participants;

        } catch (Exception e) {
            return null;
        }
    }
    public static void parseCarInfoPacket(byte[] buffer) {
        if (buffer == null || buffer.length < 500) return;

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.position(10);
            byte packetType = bb.get();


            if (packetType != PACKET_TYPE_TELEMETRY) return;


            bb.position(116);
            float fuel = bb.getFloat();
            if (Float.isNaN(fuel) || fuel < 0) fuel = 0.0f;


            bb.position(88);
            float distanceMeters = bb.getFloat();

            if (distanceMeters < 50 && lastDistance > 1000) {
                if (fuelAtLapStart > 0) {
                    lastCalculatedFuelPerLap = fuelAtLapStart - fuel;
                }
                fuelAtLapStart = fuel;
            }
            if (fuelAtLapStart < 0) fuelAtLapStart = fuel;
            lastDistance = distanceMeters;


            float[] wear = new float[4];
            bb.position(124);
            for (int i = 0; i < 4; i++) {
                float rawWear = bb.getFloat();
                wear[i] = Math.max(0.0f, (1.0f - rawWear) * 100.0f);
            }


            float[] temp = new float[4];
            bb.position(188);
            for (int i = 0; i < 4; i++) {
                float tyreTemp = bb.getFloat();
                temp[i] = (Float.isNaN(tyreTemp) || tyreTemp < 0) ? 0.0f : tyreTemp;
            }


            float[] pressure = new float[4];
            bb.position(220);
            for (int i = 0; i < 4; i++) {
                float pressBar = bb.getFloat() / 100.0f;
                pressure[i] = Math.max(0.0f, pressBar);
            }


            bb.position(496);
            int crashState = bb.get() & 0xFF;
            int aeroDamage = Math.min(crashState * 20, 100);


            HudApplication app = HudApplication.getInstance();
            if (app != null) {
                app.updateCarStatus(fuel, lastCalculatedFuelPerLap, wear, temp, pressure, aeroDamage);
            }

        } catch (Exception e) {
            System.err.println("[CarInfo Error] " + e.getMessage());
        }
    }

    public static void parseParticipantsNamesPacket(byte[] buffer) {
        if (buffer == null || buffer.length < 20) return;

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.position(10);
            byte packetType = bb.get();
            if (packetType != PACKET_TYPE_PARTICIPANTS) return;

            bb.position(12);
            byte numParticipants = bb.get();

            int offset = 16;
            for (int i = 0; i < Math.min(numParticipants, 32); i++) {
                if (offset + 64 > buffer.length) break;

                byte[] nameBytes = new byte[64];
                System.arraycopy(buffer, offset, nameBytes, 0, 64);

                int len = 0;
                while (len < 64 && nameBytes[len] != 0) len++;

                String name = new String(nameBytes, 0, len, StandardCharsets.UTF_8).trim();
                if (!name.isEmpty()) {
                    driverNames[i] = name;
                }
                offset += 64;
            }
        } catch (Exception ignored) {}
    }

}