package common;

/**
 * Constants for floor, elevator, and scheduler systems.
 */
public class Config {
    /** The time in milliseconds required to load/unload passengers into/from an elevator. */
    public static final int LOAD_TIME = 1000;

    /** The time in milliseconds required to open/close an elevator door. */
    public static final int DOOR_TIME = 3000;

    /** The number of floors traveled per second in an elevator. */
    public static final double FLOORS_PER_SECOND = 2;

    /** */
    public static final int TRANSIENT_FAULT_TIME = 2000;

    /** */
    public static final String TEXTURES = "res/textures";
}
