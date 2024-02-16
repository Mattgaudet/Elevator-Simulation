package floor;

import java.time.LocalTime;
import java.util.Comparator;

/**
 * Represents an entry in the CSV file. Contains information for the action an
 * elevator needs to perform.
 */
public class ElevatorRequest implements Comparable<ElevatorRequest> {

	/**
	 * The requested direction.
	 */
	public enum ButtonDirection {
		/** The requested direction is up. */
		UP,
		/** The requested direction is down. */
		DOWN,
		/** No requested direction. */
		NONE
	}

	/** The requested direction. Either up or down. */
	private ButtonDirection buttonDirection;

	/** The button ID. */
	private int buttonId;

	/** The requested floor number. */
	private int floorNumber;

	/** The time of the request. */
	private LocalTime currTime = LocalTime.now();
	/**
	 * Track whether the elevator request has been loaded with the passenger
	 */
	private boolean loaded = false;

	/**
	 * Create a new elevator request.
	 * @param buttonDirection The requested direction.
	 * @param floorNumber The current floor.
	 * @param buttonId The button ID - The button pressed inside the elevator (i.e. the designation floor)
	 * @param currTime The time of the request.
	 */
	public ElevatorRequest( LocalTime currTime, int floorNumber, ButtonDirection buttonDirection,  int buttonId) {
		this.currTime = currTime;
		this.floorNumber = floorNumber;
		this.buttonDirection = buttonDirection;
		this.buttonId = buttonId; 
	}

	/**
	 * Get the requested direction.
	 * @return The requested direction.
	 */
	public ButtonDirection getButtonDirection() {
		return this.buttonDirection;
	}

	/**
	 * Get the floor number.
	 * @return The floor number.
	 */
	public int getFloorNumber() {
		return this.floorNumber;
	}

	/**
	 * Get the button ID.
	 * @return The button ID.
	 */
	public int getButtonId() {
		return this.buttonId;
	}

	/**
	 * Set the button ID.
	 * @param newButtonId The button ID.
	 */
	public void setButtonId(int newButtonId) {
		this.buttonId = newButtonId;
	}

	/**
	 * Get the time of the request.
	 * @return The time of the request.
	 */
	public LocalTime getTime() {
		return this.currTime;
	}

	/**
	 * Set loaded to true
	 */
	public void setLoaded() {
		loaded = true;
	}

	/**
	 * Return loaded
	 * @return if the elevatorRequest is loaded
	 */
	public boolean isLoaded() {return this.loaded;}

	/**
	 * Compare ElevatorRequests by floor number
	 * @param e the ElevatorRequest to be compared.
	 * @return result of comparison
	 */
	@Override
	public int compareTo(ElevatorRequest e) {
		return Integer.compare(this.getFloorNumber(), e.getFloorNumber());
	}

}
