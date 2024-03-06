package elevator;

import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;

import common.Log;
import scheduler.Scheduler;

/**
 * The elevator system. Manages the scheduling of the elevators.
 */
public class ElevatorSubsystem implements Runnable {
    /** Listener variable */
    private RequestProcessedListener listener;

    /** The elevators to schedule. */
    private Elevator[] elevatorCars = new Elevator[10]; // 10 elevators max for now

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
        while(true) {
            // todo receive via UDP instead of this
            synchronized (this.scheduler.getRequestQueueFromScheduler()) {
                while (this.scheduler.getRequestQueueFromScheduler().isEmpty()) {
                    try {
                        this.scheduler.getRequestQueueFromScheduler().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // assign request to the elevator
                ElevatorRequest request = this.scheduler.getRequestQueueFromScheduler().remove(0);
                assignRequest(request, 0);
            }
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
     * Change the lamp status to indicate a request.
     * @param direction The direction of the request.
     */
    public void changeLampStatus(ButtonDirection direction) {
        this.scheduler.changeLampStatus(direction); // Change lamp status in scheduler
    }

    public void sendElevatorsInfo(DatagramSocket socket, InetAddress schedulerAddress, int schedulerPort) throws IOException {
        String info = "Elevator Info"; // Replace this with actual elevator information gathering logic
        byte[] sendData = info.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
        socket.send(sendPacket);
        System.out.println("Sent elevators info to Scheduler.");
    }


    public static void main(String[] args) {
        int listenPort = 6000; // Different port than Scheduler
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(5);

        try (DatagramSocket serverSocket = new DatagramSocket(listenPort)) {
            byte[] receiveData = new byte[1024]; // Buffer for incoming data

            System.out.println("ElevatorSubsystem listening on port " + listenPort);

            while (true) { // Run indefinitely
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if ("GET-INFO".equals(received.trim())) {
                    System.out.println("Received GET-INFO request");

                    // Extract the address and port of the Scheduler from the received packet
                    InetAddress schedulerAddress = receivePacket.getAddress();
                    int schedulerPort = receivePacket.getPort();

                    // Reply back to the Scheduler with the elevator info
                    elevatorSubsystem.sendElevatorsInfo(serverSocket, schedulerAddress, schedulerPort);
                } else {
                    // Handle regular elevator request, with the ID of the desired elevator
                }
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}