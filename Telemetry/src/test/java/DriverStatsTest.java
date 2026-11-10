import org.example.Participant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class DriverStatsTest {

    @Test
    public void testParticipantStatsAndPosition() {

        Participant driver = new Participant(12,230.0f,23333);

        System.out.println(driver.getPosition());
        int calculatedPos = driver.getPosition();
        assertTrue(calculatedPos == 12, "1");


        assertEquals(driver.getCurrentTime() > 230, "true");


        assertFalse(Float.isNaN(driver.getLastLapTime()),"3" );
        assertFalse(driver.getLastLapTime() <= 2333333, "4");
    }

    @Test
    public void testListProcessing() {
        List<Participant> list = new ArrayList<>();
        list.add(new Participant( 12,234,23333));


        assertFalse(list.isEmpty(), "null");

        Participant topDriver = list.get(0);
        assertTrue(topDriver.getPosition() == 12, "12");
    }
    @Test
    public void DriverStatsW() {

    }
}