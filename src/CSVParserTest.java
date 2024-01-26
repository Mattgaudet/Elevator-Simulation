import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVParserTest {

    @org.junit.jupiter.api.Test
    void parseCSVTest() {
        CSVParser parser = new CSVParser();
        List<ElevatorPacket> elevatorPacketList = parser.parseCSV("floors_data.csv");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss:SSS");
        String formattedTime = elevatorPacketList.get(0).getTime().format(formatter);

        assertEquals("2:05:15:000", formattedTime);
        assertEquals(2, elevatorPacketList.get(0).getFloor());
        assertEquals("Up", elevatorPacketList.get(0).getFloorButton());
        assertEquals(4, elevatorPacketList.get(0).getCarButton());
        assertEquals(0, elevatorPacketList.get(0).getElevatorId());
    }
}