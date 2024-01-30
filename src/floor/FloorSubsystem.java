package floor;

import log.Log;

import scheduler.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import floor.ElevatorRequest.ButtonDirection;

public class FloorSubsystem implements Runnable {
    private final Scheduler scheduler;
    private final List<ElevatorRequest> elevatorRequests;
    private final Floor[] floorArray;
    private ButtonDirection direction;

    public FloorSubsystem() {
        scheduler = new Scheduler(this);
        elevatorRequests = new ArrayList<>();
        floorArray = new Floor[0];

    }

    public void addIn(ElevatorRequest buttonPress) {
        elevatorRequests.add(buttonPress);
    }

    public void removeOut(int index) {
        elevatorRequests.remove(index);
    }

    public List<ElevatorRequest> getAllElevatorRequestsFromFloorSubsystem() {
        return elevatorRequests;
    }

    public void changeLampStatus(ButtonDirection direction) {
        for (Floor floor : floorArray) {
            floor.changeLampStatus(direction);
        }
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(new File("res/input.txt"))) {
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

                // 2. floor number
                int floorNumber;
                try {
                    floorNumber = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    Log.print("Invalid floor number: " + parts[1].trim());
                    continue;
                }

                // 3. direction
                String directionString = parts[2].trim().toLowerCase();
                switch (directionString) {
                    case "up":
                        direction = ButtonDirection.UP;
                        break;
                    case "down":
                        direction = ButtonDirection.DOWN;
                        break;
                    default:
                        Log.print("Invalid direction: " + directionString);
                        continue;
                }

                // 4. car/Elevator number
                int carNumber;
                try {
                    carNumber = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    Log.print("Invalid car number: " + parts[3].trim());
                    continue;
                }

                // Create ElevatorRequest
                ElevatorRequest elevatorRequest = new ElevatorRequest(direction, floorNumber, carNumber, time);

                // Print request
                Log.print(
                        "FloorSubsystem: Read ElevatorRequest(" + elevatorRequest + ") from File >> " + "Time: " + time
                                + ", Floor: " + floorNumber + ", Direction: " + direction + ", Car: " + carNumber);

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

    }
}