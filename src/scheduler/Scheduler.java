package scheduler;

import elevator.Elevator;
import elevator.ElevatorInfo;
import floor.ElevatorRequest;
import floor.FloorSubsystem;
import floor.ElevatorRequest.ButtonDirection;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Communicates between the ElevatorSubsystem and the FloorSubsystem. The Scheduler
 * notifies the Elevator when there is work to be done. After data is sent to the Elevator,
 * it forwards the data to the Floor.
 */
public class Scheduler implements Runnable {
    /** The requests to forward to the elevator subsystem. */
    private ArrayList<ElevatorRequest> schedulerRequestsQueue = new ArrayList<>();

    /** The requests to forward to the floor subsystem. */
    private FloorSubsystem floorSubsystem;
    
    /** The state of the scheduler. */
    SchedulerState state;

    /** The requests to forward to the floor subsystem. */
    private ArrayList<ElevatorRequest> schedulerResponseLog = new ArrayList<>();

    public Scheduler() {
    }

    /**
     * Create a new scheduler.
     * @param floorSubsystem The floor subsystem for the scheduler.
     */
    public Scheduler(FloorSubsystem floorSubsystem) {
        this.floorSubsystem = floorSubsystem;
    }

        /**
     * Create a new scheduler with a specified queue of requests.
     * @param floorSubsystem The floor subsystem for the scheduler.
     * @param requestsQueue The queue of requests.
     */
    public Scheduler(FloorSubsystem floorSubsystem, ArrayList<ElevatorRequest> requestsQueue) {
        this.floorSubsystem = floorSubsystem;
        this.schedulerRequestsQueue = requestsQueue;
    }

    //  Set the state of the scheduler.
    public void setState(SchedulerState state) {
        this.state = state;
    }

    //  * Get the queue of requests.
    //  * @return The queue of requests.
    
    public ArrayList<ElevatorRequest> getRequestQueueFromScheduler() {
        return schedulerRequestsQueue;
    }

    /**
     * Get the responses.
     * @return The responses.
     */
    public ArrayList<ElevatorRequest> getSchedulerResponseLog() {
        return schedulerResponseLog;
    }

    /**
     * Add a request to the scheduler.
     * @param elevatorRequest The request to add to the scheduler.
     */
    public void addToRequestQueue(ElevatorRequest elevatorRequest) {
        synchronized (schedulerRequestsQueue) {
            schedulerRequestsQueue.add(elevatorRequest);
            schedulerRequestsQueue.notifyAll();
        }
    }

    /**
     * This is asking the ElevatorSubsystem for the Elevators info
     * It sends a UDP request "GET-INFO" to the ElevatorSubsystem
     * and gets a response back with the info
     */
    public String getElevatorsInfo() {
        String getInfoRequest = "GET-INFO";
        int elevatorSubsystemPort = 6000; // The port the ElevatorSubsystem is listening on
        String elevatorSubsystemHost = "localhost"; // Assuming the ElevatorSubsystem is on the same host
        String elevatorsInfo = null;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress elevatorSubsystemAddress = InetAddress.getByName(elevatorSubsystemHost);
            byte[] sendData = getInfoRequest.getBytes();

            // Send the GET-INFO request
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, elevatorSubsystemAddress, elevatorSubsystemPort);
            socket.send(sendPacket);

            // Prepare to receive the response
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            // Process the received data
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received elevators info: " + response);
            elevatorsInfo = Arrays.toString(parseElevatorsInfo(receivePacket.getData()));

        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return elevatorsInfo;
    }

     /**
     * This parses the info received from the ElevatorSubsystem
     * about all the elevator cars and their current status
     * @param data the info received via UDP in the bytes format
     * @return an array of ElevatorInfo objects representing the current state of elevators
     */
    public ElevatorInfo[] parseElevatorsInfo(byte[] data) {
        String receivedInfo = new String(data);
        String[] elevatorsInfo = receivedInfo.split("\n"); // Each elevator info is separated by a newline

        List<ElevatorInfo> elevatorInfoList = new ArrayList<>();

        for (String elevatorInfo : elevatorsInfo) {
            String[] info = elevatorInfo.split(";");
            if (info.length == 4) { 
                int elevatorId = Integer.parseInt(info[0]);
                String stateString = info[1];
                Elevator.State currentState;

                try {
                    currentState = Elevator.State.valueOf(stateString);
                } catch (IllegalArgumentException e) {
   
                    System.err.println("Invalid elevator state: " + stateString);
                    currentState = Elevator.State.UNKNOWN;
                }

                int currentFloor = Integer.parseInt(info[2]);
                ButtonDirection currDirection = ButtonDirection.valueOf(info[3]);

                // Create an ElevatorInfo object for each elevator and add to the list
                ElevatorInfo elevatorInfoObj = new ElevatorInfo(elevatorId, currentState, currentFloor, currDirection);
                elevatorInfoList.add(elevatorInfoObj);
            }
        }

        // Convert the list to an array
        return elevatorInfoList.toArray(new ElevatorInfo[0]);
    }


    /**
     * Change the lamp directional status of the floor subsystem.
     * @param direction The lamp directional status.
     */
    public void changeLampStatus(ButtonDirection direction) {
        floorSubsystem.changeLampStatus(direction);
    }


    /**
     * The entrypoint of the scheduler. Continuously checks if there are any jobs in the
     * FloorSubsystem. If there are, it adds the jobs to the queue and removes it from the
     * FloorSubsystem.
     */
    @Override
    public void run() {
        // set the initial state to IDLE
        setState(new AwaitingRequestState(this));
        
        int listenPort = 5000;
        try (DatagramSocket serverSocket = new DatagramSocket(listenPort)) {
            System.out.println("Scheduler listening on port " + listenPort);
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                
                // Parse the received data into an ElevatorRequest object
                ElevatorRequest request = new ElevatorRequest(receivePacket.getData());
                
                if (request.isProcessed()) {
                    // Handle completed request from the ElevatorSubsystem
                    System.out.println("Received completed request from ElevatorSubsystem: " + request);
                    // Further processing...
                } else {
                    // Handle new request from the FloorSubsystem
                    System.out.println("Received new request from FloorSubsystem: " + request);
                    state.processRequest(this, receivePacket.getData());
                }
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
    



    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Thread schedulerThread = new Thread(scheduler, "Scheduler Thread");
        schedulerThread.start();
    }

}