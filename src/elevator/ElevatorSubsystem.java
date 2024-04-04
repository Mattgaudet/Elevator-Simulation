package elevator;

import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import common.Log;
import scheduler.Scheduler;

/**
 * The elevator system. Manages the scheduling of the elevators.
 */
public class ElevatorSubsystem implements Runnable {
    /** Listener variable */
    private RequestProcessedListener listener;

    /** The elevators to schedule. */
    private Elevator[] elevatorCars = new Elevator[5]; // 10 elevators max for now

    /** The schedule to receive requests from. */
    private Scheduler scheduler;

    /** The requests from the scheduler. */
    private ArrayList<ElevatorRequest> elevatorSubsystemRequestsQueue = new ArrayList<ElevatorRequest>();

    /** The responses from the elevators. */
    private ArrayList<ElevatorRequest> elevatorSubsystemResponseLog = new ArrayList<ElevatorRequest>();

    /**
     * Set the listener for request processing.
     * @param listener The listener to set.
     */
    public void setListener(RequestProcessedListener listener) {
        this.listener = listener;
    }

    /**
     * Listener interface for notifying when a request is processed
     */
    public interface RequestProcessedListener {
        void onRequestProcessed();
    }

    /**
     * Create a new elevator subsystem. Creates create and start specified number of elevators
     * @param scheduler The scheduler to use for requests.
     * @param numElevators The number of elevators to create
     */
    public ElevatorSubsystem(Scheduler scheduler, int numElevators) {
        this.scheduler = scheduler;
        for(int i = 0; i < numElevators; i++) { //create and start all elevators
            this.elevatorCars[i] = new Elevator(i, this);
            this.elevatorCars[i].start();
        }
    }

    /**
     * Create a new elevator subsystem. Creates create and start specified number of elevators
     * @param numElevators The number of elevators to create
     */
    public ElevatorSubsystem(int numElevators) {
        for(int i = 0; i < numElevators; i++) { //create and start all elevators
            this.elevatorCars[i] = new Elevator(i, this);
            this.elevatorCars[i].start();
        }
    }

    /**
     * Add a request to specified elevator's queue
     * @param er elevatorRequest to be added
     * @param elevatorId elevator number to use
     */
    public void assignRequest(ElevatorRequest er, int elevatorId) {
        this.elevatorCars[elevatorId].addRequestToElevatorQueue(er);
    }
    /**
     * The entrypoint of the system. Pulls messages from the scheduler, forwards
     * them to the elevators, and sends them back to the scheduler.
     */
    public void run() {
        int listenPort = 6000; // Different port than Scheduler

        try (DatagramSocket serverSocket = new DatagramSocket(listenPort)) {
            byte[] receiveData = new byte[1024]; // Buffer for incoming data

            System.out.println("ElevatorSubsystem listening on port " + listenPort);

            while (true) { // Run indefinitely
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if ("GET-INFO".equals(received.trim())) { // received an info request from Scheduler
                    System.out.println("Received GET-INFO request");

                    // Extract the address and port of the Scheduler from the received packet
                    InetAddress schedulerAddress = receivePacket.getAddress();
                    int schedulerPort = receivePacket.getPort();

                    // Reply back to the Scheduler with the elevator info
                    sendElevatorsInfo(serverSocket, schedulerAddress, schedulerPort);
                } else { // received a request from the scheduler, with an elevator ID appended
                   
                // HANDLE FAULTS
                    // if the received message contains "DEATH" fault then set the elevator to FAULT state
                    if (received.contains("DEATH"))  {
                        int elevatorID = ByteBuffer.wrap(receiveData, receivePacket.getLength() - 4, 4).getInt();
                        System.out.println(">> Elevator " + elevatorID + " has encountered some unexpected fault!! :( ");


                            // Send the FAULT to the FloorSubsystem
                            String InfoString = "DEATH" + " fault encountered by Elevator " + elevatorID + " at floor " + elevatorCars[elevatorID].getCurrentFloor();
                            byte[] directionBytes = InfoString.getBytes(StandardCharsets.UTF_8);
                            InetAddress address = null;
                            try {
                                address = InetAddress.getByName("localhost");
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } // the FloorSubsystem listen address
                            int port = 12345; // the FloorSubsystem listen port
                            DatagramPacket packet = new DatagramPacket(directionBytes, directionBytes.length, address, port);
                            try (DatagramSocket socket = new DatagramSocket()) {
                                socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                        System.out.println("Setting elevator " + elevatorID + " to FAULT state");
                        elevatorCars[elevatorID].setState(Elevator.State.FAULT);
                    }
                    else if (received.contains("BAD_REQUEST")) { //ignore bad requests
                        Log.print(">> ElevatorSubsystem: received BAD_REQUEST, ignoring request");
                    }
                    else {
                        // Extract elevator ID from the end of the received packet
                        int elevatorID = ByteBuffer.wrap(receiveData, receivePacket.getLength() - 4, 4).getInt();

                        // Exclude the last 4 bytes (elevator ID) from the received data
                        byte[] requestData = Arrays.copyOf(receiveData, receivePacket.getLength() - 4);

                        // Create new request from bytes
                        ElevatorRequest request = new ElevatorRequest(requestData);
                        System.out.println("Received Elevator request: " + request + " assigned to elevator " + elevatorID);
                        assignRequest(request, elevatorID);
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Get the requests from the scheduler for testing.
     * @return The requests from the scheduler for testing.
     */
    public ArrayList<ElevatorRequest> getElevatorSubsystemRequestsQueue() {
        return this.elevatorSubsystemRequestsQueue;
    }

    /**
     * Get the requests from the elevators for testing.
     * @return The requests from the elevators for testing.
     */
    public ArrayList<ElevatorRequest> getElevatorSubsystemResponseLog() {
        return this.elevatorSubsystemResponseLog;
    }

    /**
     * Add an elevator response.
     * @param elevatorRequest The elevator response.
     */
    public void addResponseList(ElevatorRequest elevatorRequest) {
        synchronized (this.elevatorSubsystemResponseLog) {
            this.elevatorSubsystemResponseLog.add(elevatorRequest); // Add response to response list
            this.elevatorSubsystemResponseLog.notifyAll(); // Notify all threads waiting on response list
        }
    }
    /**
     * Return the list of elevators (for unit tests)
     */
    public Elevator[] getElevatorCars() {
        return elevatorCars;
    }

    /**
     * Change the lamp status to indicate a request.
     * @param direction The direction of the request.
     */
    public void changeLampStatus(ButtonDirection direction) {
        this.scheduler.changeLampStatus(direction); // Change lamp status in scheduler
    }

    /**
     * Sends completed requests back to the scheduler
     * @param er the completed ElevatorRequest
     */
    public void sendCompletedElevatorRequest(ElevatorRequest er) {
        byte[] sendData = er.getBytes();
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("localhost"), 5000);
            DatagramSocket serverSocket = new DatagramSocket();
            serverSocket.send(sendPacket);
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        Log.print("ElevatorSubsystem: Sent completed " + er + " to Scheduler.");
    }

    /**
     * Sends information about the current status of each elevator to the Scheduler via UDP
     * @param serverSocket socket to send from
     * @param schedulerAddress InetAddress of the Scheduler
     * @param schedulerPort port of the scheduler
     * @throws IOException
     */
    public void sendElevatorsInfo(DatagramSocket serverSocket, InetAddress schedulerAddress, int schedulerPort) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Elevator elevator : elevatorCars) { // Assuming elevatorCars is an iterable list of Elevator
            sb.append(elevator.getElevatorId())
                    .append(';')
                    .append(elevator.getCurrentState())
                    .append(';')
                    .append(elevator.getCurrentFloor())
                    .append(';')
                    .append(elevator.getCurrDirection())
                    .append('\n');
        }
        byte[] sendData = sb.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            System.err.println("IOException in sendElevatorsInfo: " + e.getMessage());
        }
        System.out.println("Sent elevators info to Scheduler.");
    }

    /**
     * Creates and starts an ElevatorSubsystem thread
     * @param args command line (not used)
     */
    public static void main(String[] args) {
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(5);
        Thread elevatorSubsystemThread = new Thread(elevatorSubsystem,"ElevatorSubsystem Thread");
        elevatorSubsystemThread.start();
    }
}