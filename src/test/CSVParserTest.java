package test;

import floor.CSVParser;
import floor.ElevatorRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVParserTest {

    @org.junit.jupiter.api.Test
    void parseCSVTest() {
        // testing
        CSVParser parser = new CSVParser();
        List<ElevatorRequest> elevatorRequestList = parser.parseCSV("res/input.csv");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss:SSS");
        String formattedTime = elevatorRequestList.get(0).getTime().format(formatter);

        assertEquals("14:05:15:000", formattedTime);
        assertEquals(4, elevatorRequestList.get(0).getFloorNumber());
        assertEquals(2, elevatorRequestList.get(0).getButtonId());
        assertEquals("UP", elevatorRequestList.get(0).getButtonDirection().toString());

    }
}