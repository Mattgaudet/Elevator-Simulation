package floor;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import log.Log;

/**
 * The CSV parser for the elevator requests.
 */
public class CSVParser {

    /** The time format in the elevator requests. */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss:SSS");

    /**
     * Parse a CSV file and create a list of elevator requests. Expects a CSV file in the format:
     * - time
     * - floor number
     * - direction
     * - elevator number
     * @param filePath The path to the CSV file.
     * @return The list of elevator requests.
     */
    public List<ElevatorRequest> parseCSV(String filePath) {
        List<ElevatorRequest> elevatorRequests = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");

                // 1. time
                String timeString = parts[0].trim();
                LocalTime time;
                try {
                    time = LocalTime.parse(timeString);
                } catch (DateTimeParseException e) {
                    Log.print("Invalid time: " + timeString);
                    continue;
                }

                // 2. floor number (i.e. The current floor)
                int floorNumber;
                try {
                    floorNumber = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    Log.print("Invalid floor number: " + parts[1].trim());
                    continue;
                }

                // 3. direction
                String directionString = parts[2].trim().toLowerCase();

                ElevatorRequest.ButtonDirection direction;
                switch (directionString) {
                    case "up":
                        direction = ElevatorRequest.ButtonDirection.UP;
                        break;
                    case "down":
                        direction = ElevatorRequest.ButtonDirection.DOWN;
                        break;
                    default:
                        Log.print("Invalid direction: " + directionString);
                        continue;
                }

                // 4. Button press inside elevator (i.e. The designation floor)
                int buttonID;
                try {
                    buttonID = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    Log.print("Invalid button number: " + parts[3].trim());
                    continue;
                }

                // Create ElevatorRequest
                ElevatorRequest elevatorRequest = new ElevatorRequest(time, floorNumber, direction, buttonID);

                // Print request
                Log.print(
                        "FloorSubsystem: Read ElevatorRequest(" + elevatorRequest + ") from File >> " + "Time: " + time
                                + ", CurrentFloor: " + floorNumber + ", Direction: " + direction + ", ButtonPress/Destination: " + buttonID);

                // To be removed (for debug only)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Add request to list
                elevatorRequests.add(elevatorRequest);

            }

            // Notify all threads once the file has been read
            synchronized (elevatorRequests) {
                elevatorRequests.notifyAll();
            }

            // Close the scanner
            scanner.close();

        } catch (FileNotFoundException e) {
            Log.print("Error: File not found");
            e.printStackTrace();
        }

        return elevatorRequests;
    }

// Unused for now:
//    private String formatTime(String timeString) {
//        // Split the time string to separate the milliseconds part
//        String[] parts = timeString.split(":");
//        if (parts.length == 4) {
//            // Ensure the milliseconds part has 3 digits
//            parts[3] = String.format("%-3s", parts[3]).replace(' ', '0');
//            return String.join(":", parts);
//        }
//        return timeString; // Return original string if not in expected format
//    }
//
//    public static void main(String[] args) {
//
//        // You can now work with the parsed data
//    }
}

