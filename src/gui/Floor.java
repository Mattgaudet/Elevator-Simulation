package gui;

import java.awt.Container;

/**
 * 
 */
public class Floor {
    /** */
    private Resource leftFloor;
    /** */
    private Resource rightFloor;
    /** */
    private Lamp upLamp;
    /** */
    private Lamp downLamp;

    /**
     * 
     * @param container
     * @param index
     */
    public Floor(Container container, int index) {
        int x1 = ResourceLoader.getWidth(ResourceType.FLOOR);
        int x2 = x1 + Elevator.getWidth() * GUI.ELEVATORS;
        int y = index * getHeight();
        leftFloor = new Resource(ResourceType.FLOOR, 0, y);
        rightFloor = new Resource(ResourceType.FLOOR, x2, y);
        upLamp = new Lamp(x1 - 6, y + getHeight() / 2 + 2);
        downLamp = new Lamp(x1 - 6, y + getHeight() / 2 - 2);
        container.add(upLamp);
        container.add(downLamp);
        container.add(leftFloor);
        container.add(rightFloor);
    }

    /**
     * 
     * @param direction
     * @param incoming
     */
    public void notifyLamp(int direction, boolean incoming) {
        if (direction == 1) {
            upLamp.notify(incoming);
        } else {
            downLamp.notify(incoming);
        }
    }

    /**
     * 
     * @return
     */
    public static int getWidth() {
        return ResourceLoader.getWidth(ResourceType.FLOOR);
    }

    /**
     * 
     * @return
     */
    public static int getHeight() {
        return ResourceLoader.getHeight(ResourceType.FLOOR);
    }
}
