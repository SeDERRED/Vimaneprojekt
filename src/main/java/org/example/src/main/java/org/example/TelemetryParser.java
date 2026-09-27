package org.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TelemetryParser {

    private static final int PACKET_TYPE_TELEMETRY = 0;

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
}