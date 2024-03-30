package test;

import floor.CSVParser;
import floor.ElevatorRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CSVParserTest {

    @Test
    void testParseCSV() {
        // testing
        List<ElevatorRequest> elevatorRequestList = CSVParser.parseCSV("res/input.csv");

        String formattedTime = elevatorRequestList.get(0).getTime().format(CSVParser.TIME_FORMATTER);

        assertEquals("14:15:05:000", formattedTime);
        assertEquals(2, elevatorRequestList.get(0).getFloorNumber());
        assertEquals(4, elevatorRequestList.get(0).getButtonId());
        assertEquals("UP", elevatorRequestList.get(0).getButtonDirection().toString());
    }

    @Test
    void testParseAndSortCSV() {
        List<ElevatorRequest> ers = CSVParser.parseAndSortCSV("res/test_input1.csv");
        
        assertEquals(2, ers.get(0).getFloorNumber());
        assertEquals(4, ers.get(1).getFloorNumber());
        assertEquals(1, ers.get(2).getFloorNumber());
    }

    @Test
    void testParseCSVWithFaults() {
        // testing
        List<ElevatorRequest> elevatorRequestList = CSVParser.parseCSV("res/input_faults.csv");

        assertEquals("BAD_REQUEST", elevatorRequestList.get(0).getFault());
        assertEquals("DOOR_NOT_CLOSE", elevatorRequestList.get(1).getFault());
        assertEquals("DEATH", elevatorRequestList.get(2).getFault());
    }

    @Test
    void testParseAndSortCSVWithFaults() {
        List<ElevatorRequest> ers = CSVParser.parseAndSortCSV("res/test_input1.csv");

        assertEquals(2, ers.get(0).getFloorNumber());
        assertEquals(4, ers.get(1).getFloorNumber());
        assertEquals(1, ers.get(2).getFloorNumber());
    }
}
