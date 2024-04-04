package gui;

import common.MathHelper;
import common.Config;
import java.awt.Color;
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
    private Number number;
    /** */
    private int floor;
    /** */
    private int i;
    /** */
    private boolean finished;
    /** */
    private boolean hardFault;

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
        number = new Number(x + 4, ResourceLoader.getHeight(ResourceType.ROOM) - 8, Color.WHITE);
        number.setSize(6);
        container.add(leftDoor);
        container.add(rightDoor);
        container.add(number);
        container.add(room);
        container.add(leftRope);
        container.add(rightRope);
        container.add(corridor);
        jobs = new LinkedBlockingDeque<>();
        thread = new Thread(() -> {
            while (true) {
                if (hardFault) {
                    return;
                }
                ElevatorJob job = null;
                while (job == null) {
                    try {
                        job = jobs.take();
                    } catch (InterruptedException e) {}
                }
                switch (job.getType()) {
                    case MOVE: handleMove(job.getData()); break;
                    case LOAD: handleLoadUnload(job.getData(), true); break;
                    case UNLOAD: handleLoadUnload(job.getData(), false); break;
                    case OPEN: handleOpenClose(true); break;
                    case CLOSE: handleOpenClose(false); break;
                    case TRANSIENT_FAULT: handleFault(false); break;
                    case HARD_FAULT: handleFault(true); break;
                }
                if (!job.isFault()) {
                    previous = job;
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
        try {
            GUI.notifyLamp(floor, direction, true);
            for (; i <= time; i += resolution) {
                if (Thread.interrupted()) {
                    return;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(resolution);
                } catch (InterruptedException e) {
                    return;
                }
                float alpha = (float) i / (float) time * floors * direction;
                setHeight(this.floor, alpha);
            }
        } finally {
            GUI.notifyLamp(floor, direction, false);
        }
        i = 0;
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
        for (; i < passengers; i++) {
            if (Thread.interrupted()) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(Config.LOAD_TIME);
            } catch (InterruptedException e) {
                return;
            }
            if (load) {
                number.addValue(1);
                GUI.take(floor, 1);
            } else {
                number.subValue(1);
                GUI.deliver(floor, 1);
            }
        }
        i = 0;
        finished = true;
    }

    /**
     * 
     * @param open
     */
    private void handleOpenClose(boolean open) {
        finished = false;
        int resolution = 10;
        for (; i <= Config.LOAD_TIME; i += resolution) {
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
        i = 0;
        finished = true;
    }

    /**
     * 
     * @param hard
     */
    private void handleFault(boolean hard) {
        if (hard) {
            setFaultTint(new Color(1.0f, 0.0f, 0.0f, 0.5f));
            hardFault = true;
            jobs.clear();
        } else {
            try {
                int resolution = 100;
                for (int i = 0; i <= Config.TRANSIENT_FAULT_TIME; i += resolution) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(resolution);
                    } catch (InterruptedException e) {}
                    float alpha = (float) i / (float) Config.TRANSIENT_FAULT_TIME;
                    float r = MathHelper.lerp(1.0f, 0.0f, alpha);
                    float g = MathHelper.lerp(0.0f, 1.0f, alpha);
                    float b = MathHelper.lerp(0.0f, 0.0f, alpha);
                    float a = MathHelper.lerp(1.0f, 0.0f, alpha);
                    setFaultTint(new Color(r, g, b, a));
                }
            } finally {
                if (previous != null && !finished) {
                    jobs.addFirst(previous);
                }
                setFaultTint(new Color(0.0f, 0.0f, 0.0f, 0.0f));
            }
        }
    }

    /**
     * 
     * @param color
     */
    private void setFaultTint(Color color) {
        leftDoor.setTint(color);
        rightDoor.setTint(color);
        room.setTint(color);
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
        number.setOffsetY(height);
    }

    /**
     * 
     * @return
     */
    public static int getWidth() {
        return ResourceLoader.getWidth(ResourceType.ROOM);
    }
}
