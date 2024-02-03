package floor;

import scheduler.Scheduler;
import floor.ElevatorRequest.ButtonDirection;
import log.Log;

// Represents a floor of a building.
// Stores basic data about the current status of the floor.
// Processes the information in a table format.

/**
 * Represents a floor of a building. Stores basic information about the current
 * status of the floor. Processes the information in a table format.
 */
public class Floor {

    /** The floor number that the floor is on. */
    private int floorNumber;

    /** Flag for when the up lamp is on. */
    private boolean isUpLampOn;

    /** Flag for when the down lamp is on. */
    private boolean isDownLampOn;

    /** The most recent button request. */
    private ElevatorRequest recentButtonPress;

    /** The scheduler to use. */
    private Scheduler scheduler;

    /**
     * Create a new floor.
     * @param floorNumber The floor number that the floor is on.
     * @param scheduler The scheduler to use.
     */
    public Floor(int floorNumber, Scheduler scheduler) {
        this.floorNumber = floorNumber;
        this.scheduler = scheduler;
    }

    /**
     * Get the floor number that the floor is on.
     * @return The floor number that the floor is on.
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Change the status of the lamp depending on the direction.
     * @param direction The direction to change the lamp status to.
     */
    public void changeLampStatus(ButtonDirection direction) {
        isUpLampOn = direction == ButtonDirection.UP;
        isDownLampOn = direction == ButtonDirection.DOWN;

        if (isUpLampOn) {
            Log.print("At Floor " + floorNumber + ", Up Lamp is On");
        } else if (isDownLampOn) {
            Log.print("At Floor " + floorNumber + ", Down Lamp is On");
        } else {
            Log.print("Both Up and Down lamps are Off");
        }
    }

    /**
     * Get the most recent button request.
     * @return The most recent button request.
     */
    public ElevatorRequest getRecentButtonPress() {
        return recentButtonPress;
    }
    
    /**
     * Check if the up lamp is on.
     * @return If the up lamp is in.
     */
    public boolean getUpLampOnStatus() {
        return isUpLampOn;
    }
    
    /**
     * Check if the down lamp is on.
     * @return If the down lamp is in.
     */
    public boolean getDownLampOnStatus() {
        return isDownLampOn;
    }
}
