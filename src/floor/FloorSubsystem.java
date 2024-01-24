package floor;

import scheduler.Scheduler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FloorSubsystem implements Runnable {
    private Thread thread;
    private Scheduler scheduler;
    private Set<Integer> upButtons;
    private Set<Integer> downButtons;
    private Set<Integer> floorLamps;
    private Map<Integer, String> directionLamps; // Map of floor number to direction
    private Set<Integer> arrivalSensors;

    public FloorSubsystem(Scheduler scheduler, int totalFloors) {
        this.scheduler = scheduler;
        upButtons = new HashSet<>();
        downButtons = new HashSet<>();
        floorLamps = new HashSet<>();
        directionLamps = new HashMap<>();
        arrivalSensors = new HashSet<>();
        for (int i = 1; i <= totalFloors; i++) {
            if (i != 1) {
                downButtons.add(i);
            }
            if (i != totalFloors) {
                upButtons.add(i);
            }
            floorLamps.add(i);
            arrivalSensors.add(i);
        }
        thread = new Thread(this);
        thread.start();
    }

    public void pressButton(int floor, String direction) {
        // Simulate button press
        if (direction.equals("up")) {
            upButtons.add(floor);
        } else {
            downButtons.add(floor);
        }
    }

    public void setFloorLamp(int floor, boolean state) {
        // Simulate setting floor lamp state
        if (state) {
            floorLamps.add(floor);
        } else {
            floorLamps.remove(floor);
        }
    }

    public void setDirectionLamp(int floor, String direction) {
        // Simulate setting direction lamp
        directionLamps.put(floor, direction);
    }

    public void setArrivalSensor(int floor, boolean state) {
        // Simulate setting arrival sensor state
        if (state) {
            arrivalSensors.add(floor);
        } else {
            arrivalSensors.remove(floor);
        }
    }



    @Override
    public void run() {
        try {
            File file = new File("src/floor/input.txt"); 
            Scanner scanner = new Scanner(file);
    
            // data columns: Time;Floor Number;Direction;Car Number
            // example data: 14:05:15.0; 2; Up; 4;
    
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                
                // Parse time
                String[] timeParts = parts[0].trim().split(":");
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1]);
                int seconds = (int) Double.parseDouble(timeParts[2]);
                int time = hours * 3600 + minutes * 60 + seconds;
                
                // Parse other data
                int floorNumber = Integer.parseInt(parts[1].trim());
                String direction = parts[2].trim().toLowerCase();
                int carNumber = Integer.parseInt(parts[3].trim());
    
                ElevatorRequest request = new ElevatorRequest(time, floorNumber, direction, carNumber);
                System.out.println("Read request from file: " + request);
    
                scheduler.addRequest(request);

                // print all data (debug)
                // System.out.println("Time: " + time);
                // System.out.println("Floor Number: " + floorNumber);
                // System.out.println("Direction: " + direction);
                // System.out.println("Car Number: " + carNumber);
                // System.out.println();
            }
    
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}



