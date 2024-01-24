import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVParserTest {

    @org.junit.jupiter.api.Test
    void parseCSVTest() {
        CSVParser parser = new CSVParser();
        List<ElevatorData> elevatorDataList = parser.parseCSV("floors_data.csv");

        assertEquals(elevatorDataList.get(0).getTime(), "2:05:15:0");
        assertEquals(elevatorDataList.get(0).getFloor(), 2);
        assertEquals(elevatorDataList.get(0).getFloorButton(), "Up");
        assertEquals(elevatorDataList.get(0).getCarButton(), 4);
        assertEquals(elevatorDataList.get(0).getElevatorId(), 0);
    }
}