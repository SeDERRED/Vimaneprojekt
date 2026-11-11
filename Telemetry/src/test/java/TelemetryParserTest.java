package org.example;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryParserTest {

    @Test
    void testParseTelemetryPacketValid() {
        // Выделяем буфер с запасом (например, 100 байт), чтобы все индексы поместились
        byte[] buffer = new byte[100];
        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);


        buffer[10] = 0;

        bb.putFloat(36, 25.0f);

        bb.putShort(40, (short) 4500);

        buffer[45] = 2;


        Telemetry telemetry = TelemetryParser.parseTelemetryPacket(buffer);

        // Проверяем, что объект успешно создался и данные распарсились верно
        assertNotNull(telemetry, "Пакет телеметрии не должен быть null");
        assertEquals(90.0f, telemetry.speedKmh(), 0.1f, "Скорость должна быть 90 км/ч");
        assertEquals(4500, telemetry.rpm(), "Обороты двигателя должны быть 4500");
        assertEquals(2, telemetry.gear(), "Передача должна быть 2");
    }

    @Test
    void testParseDriverStatsPacketValid() {
        byte[] buffer = new byte[50];
        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        // Тип пакета таймингов (3)
        buffer[10] = 3;
        // Количество участников (1)
        buffer[12] = 1;

        // Данные первого гонщика со смещения 13
        bb.putShort(13, (short) 500); // lapDistance
        buffer[15] = 1; // Позиция 1
        buffer[16] = 5; // Текущий круг 5
        bb.putFloat(17, 10.0f);       // Оставшиеся круги
        bb.putFloat(21, 2.5f);        // Gap

        List<DriverStats> statsList = TelemetryParser.parseDriverStatsPacket(buffer);

        assertNotNull(statsList);
        assertFalse(statsList.isEmpty());

        DriverStats stats = statsList.get(0);
        assertEquals(1, stats.getPosition());
        assertEquals(5, stats.getCurrentLap());
        assertEquals(10, stats.getLapsRemaining());
        assertEquals(2.5f, stats.getGapAhead(), 0.001f);
    }
}