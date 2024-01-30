package floor;

import java.time.LocalTime;

public class ElevatorRequest {

	public enum ButtonDirection {
		UP, DOWN, NONE
	}

	private ButtonDirection buttonDirection; // up is UP, down is DOWN
	private int buttonId;
	private int floorNumber;
	private LocalTime currTime = LocalTime.now(); // time of button press


	public ElevatorRequest(ButtonDirection buttonDirection, int buttonId, int floorNumber, LocalTime currTime) {
		this.buttonDirection = buttonDirection;
		this.buttonId = buttonId;
		this.floorNumber = floorNumber;
		this.currTime = currTime;
	}

	public ButtonDirection getButtonDirection() {
		return this.buttonDirection;
	}

	public int getFloorNumber() {
		return this.floorNumber;
	}

	public int getButtonId() {
		return this.buttonId;
	}

	public void setButtonId(int newButtonId) {
		this.buttonId = newButtonId;
	}

	public LocalTime getTime() {
		return this.currTime;
	}


}
