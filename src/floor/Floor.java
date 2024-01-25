package floor;

import floor.ElevatorRequest.ButtonDirection;

// Represents a floor of a building.
// Stores basic data about the current status of the floor.
// Processes the information in a table format.

public class Floor {
    
    private int floorNumber;
    private boolean isUpLampOn;
    private boolean isDownLampOn;
    private ElevatorRequest recentButtonPress;

    private scheduler.Scheduler scheduler;

    // Constructor
    public Floor(int floorNumber, scheduler.Scheduler scheduler) {
        this.floorNumber = floorNumber;
        this.scheduler = scheduler;
    }
    
    // Returns the floor number
    public int getFloorNumber() {
        return floorNumber;
    }

    // Changes the status of the lamps based on the direction
    public void changeLampStatus(ButtonDirection direction) {
        isUpLampOn = direction == ButtonDirection.UP;
        isDownLampOn = direction == ButtonDirection.DOWN;
        
        if (isUpLampOn) {
            System.out.println("At Floor " + floorNumber + ", Up Lamp is On");
        } else if (isDownLampOn) {
            System.out.println("At Floor " + floorNumber + ", Down Lamp is On");
        } else {
            System.out.println("Both Up and Down lamps are Off");
        }
    }

    // Returns the recent button press signal
    public ElevatorRequest getRecentButtonPress() {
        return recentButtonPress;
    }

}
