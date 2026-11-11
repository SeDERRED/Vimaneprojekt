package org.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class TelemetryParser {

    public static final int PACKET_TYPE_TELEMETRY = 0;
    public static final int PACKET_TYPE_PARTICIPANTS = 1;
    public static final int PACKET_TYPE_TIMINGS_3 = 3;
    public static final int PACKET_TYPE_TIMINGS_7 = 7;

    private static float currentFuel = -1.0f;
    private static float fuelAtLapStart = -1.0f;
    private static float lastCalculatedFuelPerLap = 0.0f;
    private static float lastDistance = 0.0f;
    private static int lastRecordedLap = -1;

    // Времена и позиция игрока
    private static float playerCurrentTime = 0.0f;
    private static float playerLastLapTime = 0.0f;
    private static int playerPosition = 0;

    private static int candidatePosition = 0;
    private static int candidateConfirmCount = 0;

    public static Telemetry parseTelemetryPacket(byte[] buffer) {

        if (buffer == null || buffer.length < 50) {
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.position(10);
            byte packetType = bb.get();

            if (packetType != PACKET_TYPE_TELEMETRY) {
                return null;
            }

            bb.position(36);
            float speedMps = bb.getFloat();

            if (Float.isNaN(speedMps) || Float.isInfinite(speedMps) || speedMps < 0 || speedMps > 200) {
                speedMps = 0.0f;
            }
            float speedKmh = speedMps * 3.6f;

            int rpm = bb.getShort(40) & 0xFFFF;
            if (rpm > 25000) rpm = 0;

            int gear = buffer[45] & 0x0F;

            float brake = (buffer[29] & 0xFF) / 255.0f;
            float throttle = (buffer[30] & 0xFF) / 255.0f;

            bb.position(88);
            float distanceMeters = bb.getFloat();
            if (Float.isNaN(distanceMeters) || distanceMeters < 0) {
                distanceMeters = 0.0f;
            }

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

    public static List<DriverStats> parseDriverStatsPacket(byte[] buffer) {
        if (buffer == null || buffer.length < 34) {
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.position(10);
            byte packetType = bb.get();

            if (packetType != PACKET_TYPE_TIMINGS_3 && packetType != PACKET_TYPE_TIMINGS_7) {
                return null;
            }

            List<DriverStats> driverStatsList = new ArrayList<>();

            final int PARTICIPANT_START = 13;
            final int PARTICIPANT_SIZE  = 21;

            int numParticipants = buffer[12] & 0xFF;
            if (numParticipants <= 0 || numParticipants > 64) numParticipants = 50;

            for (int i = 0; i < Math.min(numParticipants, 50); i++) {
                int base = PARTICIPANT_START + i * PARTICIPANT_SIZE;
                if (base + PARTICIPANT_SIZE > buffer.length) break;

                bb.position(base);
                short lapDistance = bb.getShort();
                int rawPos = bb.get() & 0x3F;
                int currentLap = bb.get() & 0xFF;

                // [4..7] Сколько кругов осталось (float)
                float lapsRemainingFloat = bb.getFloat(base + 4);
                int toGo = 0;
                if (!Float.isNaN(lapsRemainingFloat) && lapsRemainingFloat >= 0.0f && lapsRemainingFloat <= 500.0f) {
                    toGo = Math.round(lapsRemainingFloat);
                }

                float currentTime = 0.0f;
                // [8..11] Рабочий GAP разрыв
                float gapAhead = bb.getFloat(base + 8);

                if (Float.isNaN(gapAhead) || gapAhead < 0.0f || gapAhead > 3600.0f) {
                    gapAhead = 0.0f;
                }

                if (i == 0) {
                    if (rawPos > 0 && rawPos <= 64) {
                        if (playerPosition > 3 && rawPos == 1) {
                            if (candidatePosition == 1) {
                                candidateConfirmCount++;
                                if (candidateConfirmCount >= 3) {
                                    playerPosition = 1;
                                }
                            } else {
                                candidatePosition = 1;
                                candidateConfirmCount = 1;
                            }
                        } else {
                            playerPosition = rawPos;
                            candidatePosition = rawPos;
                            candidateConfirmCount = 0;
                        }
                    }

                    if (currentLap > 0 && currentLap < 200) {
                        if (lastRecordedLap != -1 && currentLap > lastRecordedLap) {
                            if (playerCurrentTime > 5.0f) {
                                playerLastLapTime = playerCurrentTime;
                            }
                        }
                        lastRecordedLap = currentLap;
                    }

                    driverStatsList.add(new DriverStats(
                            playerPosition,
                            currentLap,
                            toGo,
                            currentTime,
                            gapAhead,
                            playerLastLapTime
                    ));
                    break;
                }
            }

            return driverStatsList;

        } catch (Exception e) {
            return null;
        }
    }

    public static void parseCarInfoPacket(byte[] buffer) {
        // Уменьшаем минимальную длину, если пакет короче 500 байт,
        // но содержит данные телеметрии машины
        if (buffer == null || buffer.length < 300) return;

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.position(10);
            byte packetType = bb.get();

            // Если игра шлет данные CarInfo в пакетах типа 0 или других — разрешаем обработку
            // (Убираем строгий возврат, если packetType != 0, либо проверяем диапазон)

            bb.position(132);
            float fuel = bb.getFloat();
            if (!Float.isNaN(fuel) && fuel >= 0 && fuel < 500f) {
                currentFuel = fuel;
                if (fuelAtLapStart < 0) {
                    fuelAtLapStart = fuel;
                }
                if (fuel > fuelAtLapStart + 1.0f) {
                    fuelAtLapStart = fuel;
                }
            } else {
                fuel = 0.0f;
            }

            bb.position(88);
            float distanceMeters = bb.getFloat();
            if (!Float.isNaN(distanceMeters) && distanceMeters >= 0) {
                if (distanceMeters < 100 && lastDistance > 800) {
                    if (playerCurrentTime > 5.0f) {
                        playerLastLapTime = playerCurrentTime;
                    }
                }
                lastDistance = distanceMeters;
            }

            float[] wear = new float[4];
            bb.position(124);
            for (int i = 0; i < 4; i++) {
                if (bb.position() + 4 > buffer.length) break;
                float rawWear = bb.getFloat();
                wear[i] = Math.max(0.0f, (1.0f - rawWear) * 100.0f);
            }

            float[] temp = new float[4];
            bb.position(188);
            for (int i = 0; i < 4; i++) {
                if (bb.position() + 4 > buffer.length) break;
                float tyreTemp = bb.getFloat();
                temp[i] = (Float.isNaN(tyreTemp) || tyreTemp < 0) ? 0.0f : tyreTemp;
            }

            float[] pressure = new float[4];
            bb.position(220);
            for (int i = 0; i < 4; i++) {
                if (bb.position() + 4 > buffer.length) break;
                float pressBar = bb.getFloat();
                pressure[i] = Math.max(0.0f, pressBar);
            }

            int aeroDamage = 0;
            if (buffer.length > 496) {
                int crashState = buffer[496] & 0xFF;
                aeroDamage = Math.min(crashState * 20, 100);
            }

            HudApplication app = HudApplication.getInstance();
            if (app != null && app.getCarInfo() != null) {
                app.getCarInfo().updateInfo(fuel, lastCalculatedFuelPerLap, wear, temp, pressure, aeroDamage);
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

                offset += 64;
            }
        } catch (Exception ignored) {}
    }

}
