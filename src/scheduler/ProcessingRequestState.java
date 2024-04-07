package scheduler;

import common.Config;
import common.Log;
import elevator.Elevator;
import floor.ElevatorRequest;
import floor.ElevatorRequest.ButtonDirection;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This state requests elevator locations from the scheduler and then selects the best elevator to use
 */
public class ProcessingRequestState implements SchedulerState {
    @Override
    public void processRequest(Scheduler scheduler, byte[] requestData) {
        ElevatorRequest request = parseRequestFromFloorSubsystem(requestData);
        scheduler.addToRequestQueue(request);
        String elevatorsInfo = scheduler.getElevatorsInfo();
        int elevatorID = selectElevator(scheduler, request, elevatorsInfo);   // currently, always 0
        scheduler.setState(new ElevatorDispatchState());
        Log.print("Scheduler: State transitioned to ELEVATOR DISPATCH STATE.");
        scheduler.state.processRequest(scheduler, request, elevatorID);
    }

    /**
     * Create an elevatorRequest object from a byte array
     * @param requestData the request data
     * @return the new ElevatorRequest created
     */
    private ElevatorRequest parseRequestFromFloorSubsystem(byte[] requestData) {
        return new ElevatorRequest(requestData);
    }

    @Override
    public void processRequest(Scheduler scheduler, ElevatorRequest request, int elevatorID) {
        // Not needed
    }

    /**
     * Selects the appropriate elevator based on the chosen logic.
     *
     * The method considers the elevator's current state, direction, and travel time to choose
     * the most suitable elevator for the given request.
     *
     * @param request The request to be sent to the selected elevator.
     * @param elevatorsInfo Information about all elevators, including their current state,
     *                      floor, and direction, received from the ElevatorSubsystem.
     * @return The ID of the chosen elevator. Returns -1 if no suitable elevator is found.
     */
    public int selectElevator(Scheduler scheduler, ElevatorRequest request, String elevatorsInfo) {
        double speedPerFloor = Config.FLOORS_PER_SECOND;
        // Extracting elevator information from the string representation
        Pattern pattern = Pattern.compile("\\{elevatorId=(\\d+), currentState=(\\w+), currentFloor=(\\d+), currDirection=(\\w+)\\}");
        Matcher matcher = pattern.matcher(elevatorsInfo);

        // Initialize variables to track the chosen elevator and minimum travel time
        int selectedElevatorId = -1;
        Comparator<ArrayList<Integer>> listComparator = Comparator.comparing(list -> list.get(0));
        ArrayList<Integer> tier1List = new ArrayList<>();
        ArrayList<Integer> tier2List = new ArrayList<>();
        PriorityQueue<ArrayList<Integer>> tier1Queue = new PriorityQueue<>(listComparator);
        PriorityQueue<ArrayList<Integer>> tier2Queue = new PriorityQueue<>(listComparator);
        // Iterate through each elevator's information
        while (matcher.find()) {
            try {
                // Extract relevant information from the matched groups
                int elevatorId = Integer.parseInt(matcher.group(1));
                Elevator.State currentState = Elevator.State.valueOf(matcher.group(2));
                int currentFloor = Integer.parseInt(matcher.group(3));
                ButtonDirection currDirection = ButtonDirection.valueOf(matcher.group(4));

                // Calculate the travel time for the current elevator
                int travelTime = calculateTravelTime(currentFloor, request.getFloorNumber(), speedPerFloor);
                //check if elevator is idle or transporting and on the way to the elevator's destination
                if(currentState == Elevator.State.IDLE || (onTheWay(request, currentFloor, currDirection))
                        && currentState == Elevator.State.TRANSPORTING)  {
                    tier1List.add(travelTime);
                    tier1List.add(elevatorId);
                    tier1Queue.offer(tier1List);
                }
                if(currentState != Elevator.State.FAULT) {
                    tier2List.add(travelTime);
                    tier2List.add(elevatorId);
                    tier2Queue.offer(tier2List);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing elevator information: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if(!tier1Queue.isEmpty()) { //select the closest elevator that is idle or transporting in same direction
            selectedElevatorId = tier1Queue.poll().get(1);
            Log.print("Selected elevator ID is : " + selectedElevatorId);
        } else if (!tier2Queue.isEmpty()){ //otherwise select the closest elevator
            selectedElevatorId = tier2Queue.poll().get(1);
            Log.print("Selected elevator ID is : " + selectedElevatorId);
        } else {
            Log.print("Failed to find a working elevator");
        }
        return selectedElevatorId;
    }

    /**
     * Check if request in the current path of the elevator
     * @param er the request to schedule
     * @param elevatorFloor the current floor of the elevator
     * @param elevatorDirection the current direction of the elevator
     * @return true if in the path, false otherwise
     */
    private boolean onTheWay(ElevatorRequest er, int elevatorFloor, ButtonDirection elevatorDirection) {
        int reqFloor = er.getFloorNumber();
        if((reqFloor > (elevatorFloor + 1) && elevatorDirection == ButtonDirection.UP && er.getButtonDirection() == ButtonDirection.UP) ||
                (reqFloor < (elevatorFloor - 1) && elevatorDirection == ButtonDirection.DOWN && er.getButtonDirection() == ButtonDirection.DOWN)) {
            return true;
        }
        return false;
    }

    /**
     * Calculate the travel time from current floor to destination floor.
     * @param currentFloor The current floor.
     * @param destinationFloor The destination floor.
     * @param speedPerFloor The speed of the elevator per floor.
     * @return The travel time in seconds, or -1 if speedPerFloor is zero.
     */
    private int calculateTravelTime(int currentFloor, int destinationFloor, double speedPerFloor) {
        if (speedPerFloor == 0) {
            System.err.println("Error: Elevator is not moving. Speed per floor is zero.");
            return -1;
        }

        int floorsToTravel = Math.abs(destinationFloor - currentFloor);
        return (int) Math.ceil(floorsToTravel / speedPerFloor);
    }

}