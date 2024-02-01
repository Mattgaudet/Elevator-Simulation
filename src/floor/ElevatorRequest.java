package floor;

import java.time.LocalTime;

/**
 * Represents an entry in the CSV file. Contains information for the action an
 * elevator needs to perform.
 */
public class ElevatorRequest {

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
	 * Create a new elevator request.
	 * @param buttonDirection The requested direction.
	 * @param buttonId The button ID.
	 * @param floorNumber The requested floor number.
	 * @param currTime The time of the request.
	 */
	public ElevatorRequest(ButtonDirection buttonDirection, int buttonId, int floorNumber, LocalTime currTime) {
		this.buttonDirection = buttonDirection;
		this.buttonId = buttonId;
		this.floorNumber = floorNumber;
		this.currTime = currTime;
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
}
