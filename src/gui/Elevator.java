package gui;

import common.MathHelper;
import common.Config;
import java.awt.Container;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 */
public class Elevator {
    /** */
    private Thread thread;
    /** */
    private LinkedBlockingDeque<ElevatorJob> jobs;
    /** */
    private ElevatorJob previous;
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
    private Resource[] people = new Resource[GUI.PEOPLE];
    /** */
    private int floor;
    /** */
    private int heightFraction;
    /** */
    private int openCloseFraction;
    /** */
    private int passengers;
    /** */
    private int loadingPassengers;
    /** */
    private Container container;
    /** */
    private boolean finished;

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

        for (int i = 0; i < GUI.PEOPLE; i++) {
            int personWidth = ResourceLoader.getWidth(ResourceType.PERSON);
            people[i] = new Resource(ResourceType.PERSON, x + i * (personWidth + 1) + 4, 0);
        }

        container.add(leftDoor);
        container.add(rightDoor);
        container.add(room);
        container.add(leftRope);
        container.add(rightRope);
        container.add(corridor);

        jobs = new LinkedBlockingDeque<>();

        thread = new Thread(() -> {
            while (true) {
                ElevatorJob job = null;
                while (job == null) {
                    try {
                        job = jobs.take();
                    } catch (InterruptedException e) {}
                }
                System.out.println(job.getType().name());
                switch (job.getType()) {
                    case ElevatorJobType.MOVE: handleMove(job.getData()); break;
                    case ElevatorJobType.LOAD: handleLoadUnload(job.getData(), true); break;
                    case ElevatorJobType.UNLOAD: handleLoadUnload(job.getData(), false); break;
                    case ElevatorJobType.OPEN: handleOpenClose(true); break;
                    case ElevatorJobType.CLOSE: handleOpenClose(false); break;
                    case ElevatorJobType.TRANSIENT_FAULT: handleFault(false); break;
                    case ElevatorJobType.HARD_FAULT: handleFault(true); break;
                }
                if (!job.isFault()) {
                    previous = job;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        this.container = container;
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
     * @param passengers
     */
    public void unload(int passengers) {
        jobs.add(new ElevatorJob(ElevatorJobType.UNLOAD, passengers));
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
     */
    public void transientFault() {
        jobs.addFirst(new ElevatorJob(ElevatorJobType.TRANSIENT_FAULT, 0));
        thread.interrupt();
    }

    /**
     * 
     */
    public void hardFault() {
        jobs.addFirst(new ElevatorJob(ElevatorJobType.HARD_FAULT, 0));
        thread.interrupt();
    }

    /**
     * 
     * @param floor
     */
    private void handleMove(int floor) {
        finished = false;
        int floors = Math.abs(this.floor - floor);
        if (floors == 0) {
            return;
        }
        int direction = this.floor < floor ? 1 : -1;
        int time = (int) (floors * 1.0f / Config.FLOORS_PER_SECOND * 1000.0f);
        int resolution = 100;
        GUI.notifyLamp(floor, direction, true);
        for (; heightFraction <= time; heightFraction += resolution) {
            if (Thread.interrupted()) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(resolution);
            } catch (InterruptedException e) {
                return;
            }
            float alpha = (float) heightFraction / (float) time * floors * direction;
            setHeight(this.floor, alpha);
        }
        GUI.notifyLamp(floor, direction, false);
        heightFraction = 0;
        setHeight(floor, 0);
        finished = true;
    }

    /**
     * 
     * @param passengers
     * @param load
     */
    private void handleLoadUnload(int passengers, boolean load) {
        finished = false;
        for (; loadingPassengers < passengers; loadingPassengers++) {
            if (Thread.interrupted()) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(Config.LOAD_TIME / 2);
            } catch (InterruptedException e) {
                return;
            }
            if (load) {
                GUI.take(floor, 1);
                addPassengers(1);
            } else {
                GUI.deliver(floor, 1);
                addPassengers(-1);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(Config.LOAD_TIME / 2);
            } catch (InterruptedException e) {
                return;
            }
        }
        loadingPassengers = 0;
        finished = true;
    }

    /**
     * 
     * @param passengers
     */
    private void addPassengers(int passengers) {
        int i;
        for (i = 0; i < this.passengers; i++) {
            container.remove(people[i]);
        }
        container.remove(room);
        container.remove(leftRope);
        container.remove(rightRope);
        container.remove(corridor);
        this.passengers += passengers;
        for (i = 0; i < this.passengers; i++) {
            container.add(people[i]);
        }
        container.add(room);
        container.add(leftRope);
        container.add(rightRope);
        container.add(corridor);
        GUI.update();
    }

    /**
     * 
     * @param open
     */
    private void handleOpenClose(boolean open) {
        finished = false;
        int resolution = 100;
        for (; openCloseFraction <= Config.LOAD_TIME; openCloseFraction += resolution) {
            if (Thread.interrupted()) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(resolution);
            } catch (InterruptedException e) {
                return;
            }
            float alpha;
            if (open) {
                alpha = (float) openCloseFraction / (float) Config.LOAD_TIME;
            } else {
                alpha = 1 - (float) openCloseFraction / (float) Config.LOAD_TIME;
            }
            int shift = (int) MathHelper.lerp(0, ResourceLoader.getWidth(ResourceType.DOOR), alpha);
            leftDoor.setOffsetX(-shift);
            leftDoor.setLeftClip(shift);
            rightDoor.setOffsetX(shift);
            rightDoor.setRightClip(shift);
        }
        openCloseFraction = 0;
        finished = true;
    }

    /**
     * 
     * @param hard
     */
    public void handleFault(boolean hard) {
        if (hard) {
            jobs.clear();
        } else {
            try {
                Thread.sleep(Config.TRANSIENT_FAULT_TIME);
            } catch (InterruptedException e) {
                return;
            }
            if (previous != null && !finished) {
                jobs.addFirst(previous);
            }
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
        for (int i = 0; i < GUI.PEOPLE; i++) {
            people[i].setOffsetY(height);
        }
    }

    /**
     * 
     * @return
     */
    public static int getWidth() {
        return ResourceLoader.getWidth(ResourceType.ROOM);
    }
}
