package gui;

import java.awt.Container;
import java.util.ArrayList;

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
    private ArrayList<Resource> waiting;
    /** */
    private ArrayList<Resource> delivered;
    /** */
    private Container container;
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
        container.add(upLamp);
        container.add(downLamp);
        container.add(leftFloor);
        container.add(rightFloor);
        waiting = new ArrayList<>();
        delivered = new ArrayList<>();
        this.container = container;
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

    public void setWaiters(int people) {
        container.remove(upLamp);
        container.remove(downLamp);
        container.remove(leftFloor);
        int i;
        for (i = 0; i < waiting.size(); i++) {
            if (waiting.get(i) == null) {
                continue;
            }
            container.remove(waiting.get(i));
        }
        int size = waiting.size() + people;
        waiting.clear();
        int personWidth = ResourceLoader.getWidth(ResourceType.PERSON);
        for (i = 0; i < size; i++) {
            int x = x1 - i * (personWidth + 1) - 16;
            Resource person = new Resource(ResourceType.PERSON, x, y + 2);
            waiting.add(person);
            container.add(person);
        }
        container.add(upLamp);
        container.add(downLamp);
        container.add(leftFloor);
    }

    public void add(int people) {
        setWaiters(people);
    }

    /**
     * 
     * @param people
     */
    public void take(int people) {
        setWaiters(-people);
    }

    /**
     * 
     * @param people
     */
    public void deliver(int people) {
        container.remove(rightFloor);
        int i;
        for (i = 0; i < delivered.size(); i++) {
            if (delivered.get(i) == null) {
                continue;
            }
            container.remove(delivered.get(i));
        }
        int size = delivered.size() + people;
        delivered.clear();
        int personWidth = ResourceLoader.getWidth(ResourceType.PERSON);
        for (i = 0; i < size; i++) {
            int x = x2 + i * (personWidth + 1) + 16;
            Resource person = new Resource(ResourceType.PERSON, x, y + 2);
            delivered.add(person);
            container.add(person);
        }
        container.add(rightFloor);
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
