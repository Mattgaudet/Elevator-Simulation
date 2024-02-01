package scheduler;

import log.Log;

import java.time.LocalTime;
import java.util.ArrayList;

import floor.ElevatorRequest;
import floor.FloorSubsystem;
import floor.ElevatorRequest.ButtonDirection;

// Communicates between ElevatorSubsystem and FloorSubsystem
// The Scheduler notifies the Elevator when there is work to be done
// After data is sent to the Elevator, it forwards the data to the Floor

public class Scheduler implements Runnable {

    private ArrayList<ElevatorRequest> schedulerRequestsQueue = new ArrayList<>();
    private ArrayList<ElevatorRequest> schedulerResponseLog = new ArrayList<>();
    private FloorSubsystem floorSubsystem;

    // Constructor for the Scheduler class.
    public Scheduler(FloorSubsystem floorSubsystem) {
        this.floorSubsystem = floorSubsystem;
    }

    // Override constructor for the Scheduler class including a queue.
    public Scheduler(FloorSubsystem floorSubsystem, ArrayList<ElevatorRequest> requestsQueue) {
        this.floorSubsystem = floorSubsystem;
        this.schedulerRequestsQueue = requestsQueue;
    }

    // Returns the request queue
    public ArrayList<ElevatorRequest> getRequestQueueFromScheduler() {
        return schedulerRequestsQueue;
    }

    // Returns the response log.
    public ArrayList<ElevatorRequest> getSchedulerResponseLog() {
        return schedulerResponseLog;
    }

    // Adds an elevatorRequest to the request queue.
    public synchronized void addToRequestQueue(ElevatorRequest elevatorRequest) {
        schedulerRequestsQueue.add(elevatorRequest);
        Log.print("(FORWARD) Added elevator request " + elevatorRequest + " to request queue");
        notifyAll();
    }

    // Receive request from elevator - Iter 1 (back and forth communication between FloorSubsystem - Shedular<- ElevatorSubsystem)
    public synchronized void receiveRequestFromElevator(ElevatorRequest request) {
        
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

        // send the request to the floorSubsystem
        this.floorSubsystem.recieveRequestFromSchedular(request);

        notifyAll(); // Notify any threads that are waiting for new requests
    }



    // Continuously checks if there are any new jobs in the FloorSubsystem. If there
    // are, it adds the job to the buttonEventQueue and removes it from the FloorSubsystem.


    @Override
    public void run() {

        // Continuously check if there are any new jobs in the FloorSubsystem.

        synchronized (floorSubsystem.getAllElevatorRequestsFromFloorSubsystem()) {
            while (true) {
                if (floorSubsystem.getAllElevatorRequestsFromFloorSubsystem().isEmpty()) {
                    try {
                        floorSubsystem.getAllElevatorRequestsFromFloorSubsystem().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                synchronized (schedulerRequestsQueue) {

                    // Remove the ElevatorRequest from the FloorSubsystem
                    ElevatorRequest er = floorSubsystem.getAllElevatorRequestsFromFloorSubsystem().remove(0);

                    Log.print("(FORWARD) Scheduler: Received ElevatorRequest(" + er + ") from FloorSubsystem at "
                            + LocalTime.now() + ".");

                    // To be removed (for debug only)
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // add it to the queue
                    schedulerRequestsQueue.add(er);

                    // Notify all threads.
                    schedulerRequestsQueue.notifyAll();
                }


            }
        }
    }

    // Adds an elevatorRequest to the response log.
    public void addToResponseLog(ElevatorRequest elevatorRequest) {
        synchronized (schedulerResponseLog) {
            schedulerResponseLog.add(elevatorRequest);
            schedulerResponseLog.notifyAll();
        }
    }

    // Change the status of the lamp.
    public void changeLampStatus(ButtonDirection direction) {
        floorSubsystem.changeLampStatus(direction);
    }
}
