import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss:SSS");

    public List<ElevatorPacket> parseCSV(String filePath) {
        List<ElevatorPacket> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                ElevatorPacket entry = new ElevatorPacket();
                String formattedTime = formatTime(values[0].trim());
                entry.setTime(LocalTime.parse(formattedTime, TIME_FORMATTER));
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

    private String formatTime(String timeString) {
        // Split the time string to separate the milliseconds part
        String[] parts = timeString.split(":");
        if (parts.length == 4) {
            // Ensure the milliseconds part has 3 digits
            parts[3] = String.format("%-3s", parts[3]).replace(' ', '0');
            return String.join(":", parts);
        }
        return timeString; // Return original string if not in expected format
    }

    public static void main(String[] args) {

        // You can now work with the parsed data
    }
}

