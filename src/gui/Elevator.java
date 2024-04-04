package gui;

import common.MathHelper;
import common.Config;
import java.awt.Container;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 */
public class Elevator {
    /** */
    private BlockingQueue<ElevatorJob> jobs;
    /** */
    private Resource room;
    /** */
    private Resource leftDoor;
    /** */
    private Resource rightDoor;
    /** */
    private Resource leftRope;
    /** */
    private Resource rightRope;
    /** */
    private Resource corridor;
    /** */
    private int floor;

    /**
     * 
     * @param container
     * @param index
     */
    public Elevator(Container container, int index) {
        int x = Floor.getWidth() + index * getWidth();

        room = new Resource(ResourceType.ROOM, x, 0);
        leftDoor = new Resource(ResourceType.DOOR, x, 0);
        rightDoor = new Resource(ResourceType.DOOR, x + ResourceLoader.getWidth(ResourceType.DOOR), 0);
        leftRope = new Resource(ResourceType.ROPE, x + 1, 0);
        rightRope = new Resource(ResourceType.ROPE, x + ResourceLoader.getWidth(ResourceType.ROOM) - 3, 0);
        corridor = new Resource(ResourceType.CORRIDOR, x, 0);

        container.add(leftDoor);
        container.add(rightDoor);
        container.add(room);
        container.add(leftRope);
        container.add(rightRope);
        container.add(corridor);

        jobs = new LinkedBlockingQueue<>();

        Thread thread = new Thread(() -> {
            while (true) {
                ElevatorJob job = null;
                while (job == null) {
                    try {
                        job = jobs.take();
                    } catch (InterruptedException e) {}
                }
                switch (job.getType()) {
                    case ElevatorJobType.MOVE: handleMove(job.getData()); break;
                    case ElevatorJobType.LOAD: handleLoad(job.getData()); break;
                    case ElevatorJobType.OPEN: handleOpenClose(true); break;
                    case ElevatorJobType.CLOSE: handleOpenClose(false); break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 
     * @param floor
     */
    public void move(int floor) {
        jobs.add(new ElevatorJob(ElevatorJobType.MOVE, floor));
    }

    /**
     * 
     * @param passengers
     */
    public void load(int passengers) {
        jobs.add(new ElevatorJob(ElevatorJobType.LOAD, passengers));
    }

    /**
     * 
     */
    public void open() {
        jobs.add(new ElevatorJob(ElevatorJobType.OPEN, 0));
    }

    /**
     * 
     */
    public void close() {
        jobs.add(new ElevatorJob(ElevatorJobType.CLOSE, 0));
    }

    /**
     * 
     * @param floor
     */
    private void handleMove(int floor) {
        int floors = Math.abs(this.floor - floor);
        if (floors == 0) {
            return;
        }
        int direction = this.floor < floor ? 1 : -1;
        int time = (int) (floors * 1.0f / Config.FLOORS_PER_SECOND * 1000.0f);
        int resolution = 10;
        GUI.notifyLamp(floor, direction, true);
        for (int i = 0; i <= time; i += resolution) {
            try {
                TimeUnit.MILLISECONDS.sleep(resolution);
            } catch (InterruptedException e) {}
            float alpha = (float) i / (float) time * floors * direction;
            setHeight(this.floor, alpha);
        }
        GUI.notifyLamp(floor, direction, false);
        setHeight(floor, 0);
    }

    /**
     * 
     * @param passengers
     */
    private void handleLoad(int passengers) {
        try {
            TimeUnit.MILLISECONDS.sleep(passengers * Config.LOAD_TIME);
        } catch (InterruptedException e) {}
    }

    /**
     * 
     * @param open
     */
    private void handleOpenClose(boolean open) {
        int resolution = 100;
        for (int i = 0; i <= Config.LOAD_TIME; i += resolution) {
            try {
                TimeUnit.MILLISECONDS.sleep(resolution);
            } catch (InterruptedException e) {}
            float alpha;
            if (open) {
                alpha = (float) i / (float) Config.LOAD_TIME;
            } else {
                alpha = 1 - (float) i / (float) Config.LOAD_TIME;
            }
            int shift = (int) MathHelper.lerp(0, ResourceLoader.getWidth(ResourceType.DOOR), alpha);
            leftDoor.setOffsetX(-shift);
            leftDoor.setLeftClip(shift);
            rightDoor.setOffsetX(shift);
            rightDoor.setRightClip(shift);
        }
    }

    /**
     * 
     * @param floor
     * @param alpha
     */
    private void setHeight(int floor, float alpha) {
        this.floor = floor;
        int height = Floor.getHeight() * floor + (int) (Floor.getHeight() * alpha);
        room.setOffsetY(height);
        leftDoor.setOffsetY(height);
        rightDoor.setOffsetY(height);
    }

    /**
     * 
     * @return
     */
    public static int getWidth() {
        return ResourceLoader.getWidth(ResourceType.ROOM);
    }
}
