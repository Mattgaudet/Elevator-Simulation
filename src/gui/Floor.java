package gui;

import java.awt.Color;
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
    /** */
    private Number floorNumber;
    /** */
    private Number waitingNumber;
    /** */
    private Number deliveredNumber;
    /** */
    private int x1;
    /** */
    private int x2;
    /** */
    private int y;

    /**
     * 
     * @param container
     * @param index
     */
    public Floor(Container container, int index) {
        x1 = ResourceLoader.getWidth(ResourceType.FLOOR);
        x2 = x1 + Elevator.getWidth() * GUI.ELEVATORS;
        y = index * getHeight();
        leftFloor = new Resource(ResourceType.FLOOR, 0, y);
        rightFloor = new Resource(ResourceType.FLOOR, x2, y);
        upLamp = new Lamp(x1 - 6, y + getHeight() / 2 + 2);
        downLamp = new Lamp(x1 - 6, y + getHeight() / 2 - 2);
        floorNumber = new Number(4, y + getHeight() - 8, Color.WHITE);
        waitingNumber = new Number(x1 / 2 - 2, y + getHeight() - 8, Color.WHITE);
        deliveredNumber = new Number(x2 + x1 / 2 - 2, y + getHeight() - 8, Color.WHITE);
        floorNumber.setSize(6);
        waitingNumber.setSize(6);
        deliveredNumber.setSize(6);
        floorNumber.setValue(index);
        container.add(floorNumber);
        container.add(waitingNumber);
        container.add(deliveredNumber);
        container.add(upLamp);
        container.add(downLamp);
        container.add(leftFloor);
        container.add(rightFloor);
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
     * @param people
     */
    public synchronized void add(int people) {
        waitingNumber.addValue(people);
    }

    /**
     * 
     * @param people
     */
    public synchronized void take(int people) {
        waitingNumber.subValue(people);
    }

    /**
     * 
     * @param people
     */
    public synchronized void deliver(int people) {
        deliveredNumber.addValue(people);
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
