package elevator;

import log.Log;

import java.time.LocalTime;
import java.util.ArrayList;

import floor.ElevatorRequest;
import floor.ElevatorRequest.ButtonDirection;
import scheduler.Scheduler;

public class ElevatorSubsystem implements Runnable {
    private Elevator[] elevatorCars = new Elevator[1]; // 1 elevator for now
    private Scheduler scheduler; // Scheduler object
    private ElevatorRequest recentButtonEvent;
    private ArrayList<ElevatorRequest> elevatorSubsystemRequestsQueue = new ArrayList<ElevatorRequest>();
    private ArrayList<ElevatorRequest> elevatorSubsystemResponseLog = new ArrayList<ElevatorRequest>();

    public ElevatorSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.elevatorCars[0] = new Elevator(0);
    }

    public void run() {
        synchronized (this.scheduler.getRequestQueueFromScheduler()) {

            while (true) {
                if (this.scheduler.getRequestQueueFromScheduler().isEmpty()) {
                    try {
                        this.scheduler.getRequestQueueFromScheduler().wait(); // Wait for requests
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                synchronized (this.elevatorSubsystemRequestsQueue) {
                    ElevatorRequest request = this.scheduler.getRequestQueueFromScheduler().remove(0); // Remove request
                                                                                                       // from queue
                    this.elevatorSubsystemRequestsQueue.add(request); // Add request to task list

                    Log.print("(FORWARD) ElevatorSubsystem: Received ElevatorRequest(" + request + ") from Scheduler at "
                            + LocalTime.now());

                    // To be removed (for debug only)
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    this.elevatorSubsystemRequestsQueue.notifyAll(); // Notify all threads waiting on task list


                    // After processing the request, send it back to the scheduler - Iter 1
                    // (back and forth communication between FloorSubsystem <- Shedular <- ElevatorSubsystem)
                    this.scheduler.receiveRequestFromElevator(request);

                }


    



            }
        }
    }

    public ArrayList<ElevatorRequest> getElevatorSubsystemRequestsQueue() {
        return this.elevatorSubsystemRequestsQueue;
    }

    public void addResponseList(ElevatorRequest elevatorRequest) {
        synchronized (this.elevatorSubsystemResponseLog) {
            this.elevatorSubsystemResponseLog.add(elevatorRequest); // Add response to response list
            this.elevatorSubsystemResponseLog.notifyAll(); // Notify all threads waiting on response list
        }
    }

    public void changeLampStatus(ButtonDirection direction) {
        this.scheduler.changeLampStatus(direction); // Change lamp status in scheduler
    }

}
