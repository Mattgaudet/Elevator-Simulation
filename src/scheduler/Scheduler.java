package scheduler;

import elevator.Elevator;
import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;
import floor.FloorSubsystem;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import common.Log;

/**
 * Communicates between the ElevatorSubsystem and the FloorSubsystem. The Scheduler
 * notifies the Elevator when there is work to be done. After data is sent to the Elevator,
 * it forwards the data to the Floor.
 */
public class Scheduler implements Runnable {
    /** The requests to forward to the elevator subsystem. */
    private ArrayList<ElevatorRequest> schedulerRequestsQueue = new ArrayList<>();

    /** The requests to forward to the floor subsystem. */
    private ArrayList<ElevatorRequest> schedulerResponseLog = new ArrayList<>();

    /** The floor subsystem. */
    private FloorSubsystem floorSubsystem;
    private SchedulerStateMachine stateMachine = new SchedulerStateMachine();

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

    /**
     * Get the queue of requests.
     * @return The queue of requests.
     */
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
            Log.print("(FORWARD) Added elevator request " + elevatorRequest + " to request queue");
            schedulerRequestsQueue.notifyAll();
        }
    }

    /**
     * Get a request from an elevator and send the the floor subsystem.
     * @param request The request from an elevator.
     */
    public void receiveRequestFromElevator(ElevatorRequest request) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.print("(BACK) Scheduler: Received ElevatorRequest(" + request + ") BACK from ElevatorSubsystem at "
                + LocalTime.now() + ".");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (schedulerRequestsQueue) {
            // send the request to the floorSubsystem
            floorSubsystem.receiveRequestFromScheduler(request);
            schedulerRequestsQueue.remove(request); //remove from queue
            schedulerRequestsQueue.notifyAll(); // Notify any threads that are waiting for new requests
        }
    }

    /**
     * The entrypoint of the scheduler. Continuously checks if there are any jobs in the
     * FloorSubsystem. If there are, it adds the jobs to the queue and removes it from the
     * FloorSubsystem.
     */
    @Override
    public void run() {
        while (true) {

        }
    }

    /**
     * Handles the processing state of the scheduler. Processes elevator requests from the FloorSubsystem.
     */
    private void handleProcessingState() {
        // TODO: Ali
        stateMachine.startIdling();
    }

    /**
     * Handles the idle state of the scheduler. Starts processing if the current state is IDLE.
     */
    private void handleIdleState() {
        // TODO: Ali
        stateMachine.startProcessing();
    }

    /**
     * Add an elevator request to the responses.
     * @param elevatorRequest The elevator request to add.
     */
    public void addToResponseLog(ElevatorRequest elevatorRequest) {
        synchronized (schedulerResponseLog) {
            schedulerResponseLog.add(elevatorRequest);
            schedulerResponseLog.notifyAll();
        }
    }

    public ElevatorRequest parseRequestFromFloorSubsystem(byte[] requestData) {
        ElevatorRequest newRequest = new ElevatorRequest(requestData);
        return newRequest;
    }

    /**
     * This parses the info received from the ElevatorSubsystem
     * about all the elevator cars and their current status
     * @param data the info received via UDP in the bytes format
     * @return a string of format "1;elevator.ElevatorIdleState@4fca772d;0;NONE"
     * Where 1 is the elevatorID, the state, 0 is the current floor, and NONE the direction
     * TODO: Need to improve parsing so we can use more easily this info.
     */
    public String parseElevatorsInfo(byte[] data){
        String receivedInfo = new String(data);
        String[] elevatorsInfo = receivedInfo.split("\n"); // Each elevator info is separated by a newline
//
//        for (String elevatorInfo : elevatorsInfo) {
//            String[] info = elevatorInfo.split(";");
//            if (info.length == 4) { // Ensure there are exactly 4 pieces of information
//                int elevatorId = Integer.parseInt(info[0]);
//                ElevatorState currentState = ElevatorState.valueOf(info[1]);
//                int currentFloor = Integer.parseInt(info[2]);
//                ButtonDirection currDirection = ButtonDirection.valueOf(info[3]);
//
//                // Now you have the information for each elevator
//                // You can use it to update the scheduler's understanding of each elevator's state
//            }
//        }
        //return elevatorsInfo; // need to make return type String[]
        return receivedInfo;
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
            elevatorsInfo = parseElevatorsInfo(receivePacket.getData());

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
     * Selects the appropriate elevator based on the chosen logic
     * TODO: select elevator logic
     * @param request the request to be sent to the selected elevator
     * @return the ID of the chosen elevator
     */
    public int selectElevator(ElevatorRequest request){
        // get the elevator Info here and process how you want
        getElevatorsInfo(); // used the parsed info
        return 0; //
    }

    /**
     * TODO: This is the processing state, probably.
     * This is where the scheduler is selecting which elevator to send the request too
     * @param requestData the received request in bytes format via UDP
     */
    public void scheduleElevatorRequest(byte[] requestData) {
        ElevatorRequest request = parseRequestFromFloorSubsystem(requestData);
        // Print the details of the parsed request
        if (request != null) {
            System.out.println("Received and parsed request: " + request.toString());
        } else {
            System.out.println("Failed to parse the request from received data.");
        }
        addToRequestQueue(request); // reusing previous method
        int elevatorID = selectElevator(request); // currently, always 0
        // send the request to the selected elevator on the requestPort
        sendRequestToElevator(request, elevatorID);
    }

    public void sendRequestToElevator(ElevatorRequest request, int elevatorID){
        byte[] requestData = request.getBytes();
        byte[] elevatorIDData = ByteBuffer.allocate(4).putInt(elevatorID).array(); // 4 bytes for an int

        // Combine requestData and elevatorIDData into sendData
        byte[] sendData = new byte[requestData.length + elevatorIDData.length];

        System.arraycopy(requestData, 0, sendData, 0, requestData.length);
        System.arraycopy(elevatorIDData, 0, sendData, requestData.length, elevatorIDData.length);

        int elevatorSubsystemPort = 6000; // The port the ElevatorSubsystem is listening on
        String elevatorSubsystemHost = "localhost"; // Assuming the ElevatorSubsystem is on the same host

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress elevatorSubsystemAddress = InetAddress.getByName(elevatorSubsystemHost);

            // Send the Elevator request with the ID
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, elevatorSubsystemAddress, elevatorSubsystemPort);
            socket.send(sendPacket);

        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Change the lamp directional status of the floor subsystem.
     * @param direction The lamp directional status.
     */
    public void changeLampStatus(ButtonDirection direction) {
        floorSubsystem.changeLampStatus(direction);
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        int listenPort = 5000; // Port to listen for incoming requests
        try (DatagramSocket serverSocket = new DatagramSocket(listenPort)) {
            System.out.println("Scheduler listening on port " + listenPort);

            while (true) { // Run indefinitely
                byte[] receiveData = new byte[1024]; // Buffer for incoming data
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                // Handle the request
                scheduler.scheduleElevatorRequest(receivePacket.getData());
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

}