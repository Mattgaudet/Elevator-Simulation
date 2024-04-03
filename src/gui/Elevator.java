package gui;

import common.MathHelper;
import common.Config;
import java.awt.Container;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Elevator {

    private BlockingQueue<ElevatorJob> jobs;
    private Resource room;
    private Resource leftDoor;
    private Resource rightDoor;
    private Resource leftRope;
    private Resource rightRope;
    private int floor;

    public Elevator(Container container, int index) {
        int x = Floor.getLeftWidth() + index * getWidth();

        room = new Resource(ResourceType.ELEVATOR_ROOM, x, 0);
        leftDoor = new Resource(ResourceType.ELEVATOR_DOOR, x, 0);
        rightDoor = new Resource(ResourceType.ELEVATOR_DOOR, x + ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR), 0);
        leftRope = new Resource(ResourceType.ELEVATOR_ROPE, x + 1, 0);
        rightRope = new Resource(ResourceType.ELEVATOR_ROPE, x + ResourceLoader.getWidth(ResourceType.ELEVATOR_ROOM) - 2, 0);

        container.add(leftDoor);
        container.add(rightDoor);
        container.add(room);
        container.add(leftRope);
        container.add(rightRope);

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

    public void move(int floor) {
        jobs.add(new ElevatorJob(ElevatorJobType.MOVE, floor));
    }

    public void load(int passengers) {
        jobs.add(new ElevatorJob(ElevatorJobType.LOAD, passengers));
    }

    public void open() {
        jobs.add(new ElevatorJob(ElevatorJobType.OPEN, 0));
    }

    public void close() {
        jobs.add(new ElevatorJob(ElevatorJobType.CLOSE, 0));
    }

    private void handleMove(int floor) {
        int floors = Math.abs(this.floor - floor);
        if (floors == 0) {
            return;
        }
        int direction = this.floor < floor ? 1 : -1;
        int time = (int) (floors * 1.0f / Config.FLOORS_PER_SECOND * 1000.0f);
        int resolution = 10;
        for (int i = 0; i <= time; i += resolution) {
            try {
                TimeUnit.MILLISECONDS.sleep(resolution);
            } catch (InterruptedException e) {}
            float alpha = (float) i / (float) time * floors * direction;
            setHeight(this.floor, alpha);
        }
        setHeight(floor, 0);
    }

    private void handleLoad(int passengers) {
        try {
            TimeUnit.MILLISECONDS.sleep(passengers * Config.LOAD_TIME);
        } catch (InterruptedException e) {}
    }

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
            leftDoor.setOffsetX((int) MathHelper.lerp(0, -ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR), alpha));
            rightDoor.setOffsetX((int) MathHelper.lerp(0, +ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR), alpha));
        }
    }

    private void setHeight(int floor, float alpha) {
        this.floor = floor;
        int height = Floor.getHeight() * floor + (int) (Floor.getHeight() * alpha);
        room.setOffsetY(height);
        leftDoor.setOffsetY(height);
        rightDoor.setOffsetY(height);
    }

    public static int getWidth() {
        return ResourceLoader.getWidth(ResourceType.ELEVATOR_ROOM);
    }
}
