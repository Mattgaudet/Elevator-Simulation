package gui;

import java.awt.Container;

public class Floor {

    private Resource leftFloor;
    private Resource rightFloor;

    public Floor(Container container, int index) {
        int x = ResourceLoader.getWidth(ResourceType.LEFT_FLOOR) + Elevator.getWidth() * Window.ELEVATORS;
        int y = index * getHeight();
        leftFloor = new Resource(ResourceType.LEFT_FLOOR, 0, y);
        rightFloor = new Resource(ResourceType.RIGHT_FLOOR, x, y);
        container.add(leftFloor);
        container.add(rightFloor);
    }

    public static int getLeftWidth() {
        return ResourceLoader.getWidth(ResourceType.LEFT_FLOOR);
    }

    public static int getHeight() {
        return ResourceLoader.getHeight(ResourceType.LEFT_FLOOR);
    }
}
