import java.time.LocalTime;

public class ElevatorPacket {
    private LocalTime time;
    private int floor;
    private String floorButton;
    private int carButton;
    private int elevatorId;

    public ElevatorPacket() {
        this.elevatorId = 0; // Set a default for Iteration 1
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloorButton(String floorButton) {
        this.floorButton = floorButton;
    }

    public String getFloorButton() {
        return floorButton;
    }

    public void setCarButton(int carButton) {
        this.carButton = carButton;
    }

    public int getCarButton() {
        return carButton;
    }

    public void setElevatorId(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    public int getElevatorId() {
        return elevatorId;
    }
}
