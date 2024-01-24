package floor;

public class ElevatorRequest {
    private int time;
    private int floorNumber;
    private String direction;
    private int carNumber;

    public ElevatorRequest(int time, int floorNumber, String direction, int carNumber) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.carNumber = carNumber;
    }

    // Getters 
    public int getTime() {
        return this.time;
    }

    public int getFloorNumber() {
        return this.floorNumber;
    }

    public String getDirection() {
        return this.direction;
    }

    public int getCarNumber() {
        return this.carNumber;
    }

}