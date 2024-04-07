package floor;

import floor.CSVParser.ElevatorFault;
import floor.ElevatorRequest.ButtonDirection;
import scheduler.Scheduler;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import common.Log;
import java.nio.charset.StandardCharsets;
import gui.GUI;

/**
 * Create a new floor subsystem to manage the floors.
 */
public class FloorSubsystem implements Runnable {

    /** The requests from the CSV file. */
    private List<ElevatorRequest> elevatorRequests;

    /** The array of floors. */
    private final Floor[] floorArray;

    /** The total number of requests */
    private int numTotalRequests;

    /** The number of executed requests */
    private int numExecutedRequests;

    /** The starting time to subtract from the elevator request times. */
    private LocalTime baseline;

    /** The starting time for creation of the floor subsystem. */
    private LocalTime t1;

    /** The scheduler to push requests to. */
    private Scheduler scheduler;

    /**
     * Create a new floor subsystem.
     * @param filePath The CSV file path.
     */
    public FloorSubsystem(String filePath) {
        elevatorRequests = new ArrayList<>();
        floorArray = new Floor[22]; // 22 floors
        for (int i = 0; i < floorArray.length; i++) {
            floorArray[i] = new Floor(i+1); // Initialize each Floor object in the array
        }
        numExecutedRequests = 0;

        // parse CSV
        elevatorRequests = CSVParser.parseAndSortCSV(filePath);
        numTotalRequests = elevatorRequests.size();

        // handle timings
        baseline = LocalTime.now();
        t1 = LocalTime.now();

        GUI.init();
    }

    /**
     * Create a new floor subsystem with a custom baseline time.
     * @param filePath The CSV file path.
     * @param baseline The baseline time.
     */
    public FloorSubsystem(String filePath, LocalTime baseline) {
        this(filePath);
        this.baseline = baseline;
    }

    /**
     * Create a new floor subsystem.
     */
    public FloorSubsystem() {
        this("res/input.csv"); // Call the other constructor with the default file path
        // No need to initialize other fields since the other constructor does that
    }

    /**
     * Set the scheduler to push requests to.
     * @param scheduler The scheduler to push requests to.
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Wait until the elevator request's time has been reached and return the request.
     * @return The elevator request or null if there are no more.
     */
    public ElevatorRequest waitForRequestTriggered() {
        if (elevatorRequests.isEmpty()) {
            return null;
        }

        // calculate the time accrued from ctor and add to the baseline
        LocalTime t2 = LocalTime.now();
        Duration duration = Duration.between(t1, t2);
        LocalTime baseline = this.baseline.plus(duration); 

        // pop the request and wait
        ElevatorRequest er = elevatorRequests.remove(0);
        er.waitForTime(baseline);
        return er;
    }

    /**
     * Add a new elevator request.
     * @param elevatorRequest The elevator request.
     */
    public void addIn(ElevatorRequest elevatorRequest) {
        elevatorRequests.add(elevatorRequest);
        numTotalRequests++;
    }

    /**
     * Get a response from the scheduler.
     * @param elevatorRequest The response as an elevator request.
     */
    public void receiveRequestFromScheduler(ElevatorRequest elevatorRequest) {
        
        Log.print("(BACK) FloorSubsystem: Received ElevatorRequest(" + elevatorRequest + ") BACK from Scheduler at "
                + LocalTime.now());
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        numExecutedRequests++;
        // Exit if all requests have been completed
        if(numExecutedRequests == numTotalRequests) {
            Log.print("Exiting: all requests completed");
            System.exit(0);
        }
    }

    /**
     * Get all of the elevator requests.
     * @return All of the elevator requests.
     */
    public List<ElevatorRequest> getAllElevatorRequestsFromFloorSubsystem() {
        return elevatorRequests;
    }

    /**
     * Change the directional status of the lamp for all floors.
     * @param direction The directional status of the lamp for all floors.
     */
    public void changeLampStatus(ButtonDirection direction) {
        Log.print("++++++++++++++++++++++++");
        for (Floor floor : floorArray) {
            floor.changeLampStatus(direction);
        }
        Log.print("++++++++++++++++++++++++");
    }

    /**
     * The entrypoint of the system. Creates a CSV file and sends requests to the scheduler.
     */
    @Override
    public void run() {
        while (true) {
            ElevatorRequest er = waitForRequestTriggered();
            if (er == null) {
                break;
            }
            scheduler.addToRequestQueue(er);
        }
    }

    /**
     * Get the array of floors.
     * @return The array of floors.
     */
    public Floor[] getFloors() {
        return floorArray;
    }

    public static void main(String[] args) {
        String filePath = "res/input_faults.csv"; // Default file path
        //String filePath = "res/input_faults_bigger.csv"; // used for testing with more inputs
        //String filePath = "res/input_faults_bigger2.csv"; // used for testing with more inputs, spaced out more
        //String filePath = "res/input_demo.csv";
        int schedulerPort = 5000; // Example port number for Scheduler
        String schedulerHost = "localhost"; // Scheduler host, change as needed
        System.out.println("FloorSubsystem listening on port 12345");

        // Initialize the FloorSubsystem with the file path
        FloorSubsystem floorSubsystem = new FloorSubsystem(filePath, LocalTime.of(14, 15));

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress schedulerAddress = InetAddress.getByName(schedulerHost);

            // Create a DatagramSocket to receive packets (For LampStatus changes)
            DatagramSocket receiveSocket = new DatagramSocket(12345);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Create a separate thread to continuously listen for packets
            Thread receiveThread = new Thread(() -> {
                while (true) {
                    try {
                    

                        // Receive the packet (For LampStatus changes)
                        receiveSocket.receive(packet);

                        // If the packet has pattern "x;x;x;x;x;x;x;x" then it is a lamp status change trigger (example: 0;TRANSPORTING;4;UP;10;0;1)
                        if (new String(packet.getData(), 0, packet.getLength()).matches("\\d+;\\w+;\\d+;\\w+;\\d+;\\d+;\\d+")) {
                            // Process the packet (For LampStatus changes)
                            String received = new String(packet.getData(), 0, packet.getLength());
                            String[] data = received.split(";");
                            int elevator = Integer.parseInt(data[0]);
                            String state = data[1];
                            // int currentFloor = Integer.parseInt(data[2]);
                            int nextFloor = Integer.parseInt(data[2]);
                            ButtonDirection direction = ButtonDirection.valueOf(data[3]);
                            int destinationFloor = Integer.parseInt(data[4]);
                            int unloadedCount = Integer.parseInt(data[5]);
                            int loadedCount = Integer.parseInt(data[6]);

                            System.out.printf("GUI update: Elevator %d is moving %s to next floor: %d, destination floor: %d, unloaded passengers: %d, loaded passengers: %d",
                                    elevator, direction, nextFloor, destinationFloor, unloadedCount, loadedCount);
                            System.out.println();
                            floorSubsystem.changeLampStatus(direction);

                            GUI.move(elevator, nextFloor);
                            
                            // only open doors if there are passengers to unload or load
                            if (unloadedCount > 0 || loadedCount > 0) {
                                GUI.open(elevator);
                        
                                if (unloadedCount > 0) {
                                    GUI.unload(elevator, unloadedCount);
                                }
                                if (loadedCount > 0) {
                                    GUI.load(elevator, loadedCount);
                                }
                                
                                GUI.close(elevator);
                            }

                        }

                        // If the packet contains 'fault'
                        if (new String(packet.getData(), 0, packet.getLength()).contains("fault")) {
                            // Process the packet (For LampStatus changes)
                            String received = new String(packet.getData(), 0, packet.getLength());
                            System.out.println();
                            System.out.println("Received: " + received); //
                            String[] data = received.split(" ");
                            String fault = data[0];
                            int elevator = Integer.parseInt(data[5]);

                            if (ElevatorFault.fromString(fault).isHardFault()) {
                                GUI.hardFault(elevator);
                            } else {
                                switch (fault) {
                                    case "DOOR_NOT_OPEN":
                                        GUI.openFault(elevator);
                                        break;
                                    case "DOOR_NOT_CLOSE":
                                        GUI.closeFault(elevator);
                                        break;
                                }
                            }
                        }

                    } catch (IOException e) {
                        System.err.println("IOException while receiving packet: " + e.getMessage());
                    }
                }
            });

            receiveThread.start(); // Start the thread to listen for packets

            while (true) { // Loop indefinitely
                ElevatorRequest er = floorSubsystem.waitForRequestTriggered();
                if (er != null) {

                    if (ElevatorFault.fromString(er.getFault()).isHardFault()) {
                        GUI.add(er.getFloorNumber(), 1);
                    }

                    byte[] sendData = er.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
                    socket.send(sendPacket);

                    // Print the data sent, for testing
                    String sentDataString = new String(sendData, StandardCharsets.UTF_8);
                    System.out.println("Data sent: " + sentDataString + " at time: " + LocalTime.now());

                } else {
                    // If no request is available, wait a bit before checking again
                    try {
                        Thread.sleep(100); // Wait for 100 milliseconds
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                        System.err.println("Interrupted while waiting for a request.");
                        break; // Exit the loop if the thread is interrupted
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

}


