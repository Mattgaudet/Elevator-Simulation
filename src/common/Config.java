package common;

/**
 * Constants for floor, elevator, and scheduler systems.
 */
public class Config {
    /** The time in milliseconds required to load/unload passengers into/from an elevator. */
    public static final int LOAD_TIME = 5000; //5 seconds

    /** The time in milliseconds required to open/close an elevator door. */
    public static final int DOOR_TIME = 3000; //3 seconds

    /** The number of floors traveled per second in an elevator. */
    public static final double FLOORS_PER_SECOND = 2;

    /** The time in milliseconds required to travel one floor. */
    public static final int TIME_TO_TRAVEL_1_FLOOR = 10000; //10 seconds

    /** */
    public static final int TRANSIENT_FAULT_TIME = 20000; //20 seconds

    /** */
    public static final String TEXTURES = "res/textures";
    public static final int MAX_PASSENGERS = 5;
}
