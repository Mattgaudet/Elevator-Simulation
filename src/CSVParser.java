import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    public List<ElevatorData> parseCSV(String filePath) {
        List<ElevatorData> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                ElevatorData entry = new ElevatorData();
                entry.setTime(values[0]);
                entry.setFloor(Integer.parseInt(values[1].trim()));
                entry.setFloorButton(values[2]);
                entry.setCarButton(Integer.parseInt(values[3].trim()));
                entry.setElevatorId(values.length > 4 ? Integer.parseInt(values[4]) : 0); // Handle missing values
                data.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        CSVParser parser = new CSVParser();
        List<ElevatorData> elevatorDataList = parser.parseCSV("floors_data.csv");
        // You can now work with the parsed data
    }
}

