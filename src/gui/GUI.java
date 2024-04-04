package gui;

/**
 * 
 */
public class GUI {
    /** */
    public static final int FLOORS = 22;
    /** */
    public static final int ELEVATORS = 4;
    /** */
    public static final int WIDTH = 312;
    /** */
    public static final int HEIGHT = 572;
    /** */
    public static final int PEOPLE = 5;
    /** */
    private static Window window;
    /** */
    private static Elevator[] elevators = new Elevator[ELEVATORS];
    /** */
    private static Floor[] floors = new Floor[FLOORS];

    /**
     * 
     */
    public static void init() {
        window = new Window(WIDTH, HEIGHT);
        int i;
        for (i = 0; i < ELEVATORS; i++) {
            elevators[i] = new Elevator(window, i);
        }
        for (i = 0; i < FLOORS; i++) {
            floors[i] = new Floor(window, i);
        }
    }

    /**
     * 
     * @param floor
     * @param direction
     * @param incoming
     */
    public static void notifyLamp(int floor, int direction, boolean incoming) {
        floors[floor].notifyLamp(direction, incoming);
    }

    /**
     * 
     * @param floor
     * @param people
     */
    public static void add(int floor, int people) {
        floors[floor].add(people);
    }

    /**
     * 
     * @param floor
     * @param people
     */
    public static void take(int floor, int people) {
        floors[floor].take(people);
    }

    /**
     * 
     * @param floor
     * @param people
     */
    public static void deliver(int floor, int people) {
        floors[floor].deliver(people);
    }

    /**
     * 
     * @param elevator
     * @param floor
     */
    public static void move(int elevator, int floor) {
        elevators[elevator].move(floor);
    }

    /**
     * 
     * @param elevator
     * @param passengers
     */
    public static void load(int elevator, int passengers) {
        elevators[elevator].load(passengers);
    }

    /**
     * 
     * @param elevator
     * @param passengers
     */
    public static void unload(int elevator, int passengers) {
        elevators[elevator].unload(passengers);
    }

    /**
     * 
     * @param elevator
     */
    public static void open(int elevator) {
        elevators[elevator].open();
    }

    /**
     * 
     * @param elevator
     */
    public static void close(int elevator) {
        elevators[elevator].close();
    }

    /**
     * 
     * @param elevator
     */
    public static void transientFault(int elevator) {
        elevators[elevator].transientFault();
    }

    /**
     * 
     * @param elevator
     */
    public static void hardFault(int elevator) {
        elevators[elevator].hardFault();
    }

    /**
     * 
     */
    public static void update() {
        window.repaint();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        init();

        add(4, 5);
        move(1, 4);
        open(1);
        load(1, 5);
        close(1);
        move(1, 9);
        open(1);
        unload(1, 3);
        close(1);
        move(1, 12);
        open(1);
        unload(1, 2);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            transientFault(1);
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {}
            transientFault(1);
        }).start();

        move(2, 20);
        new Thread(() -> {
            try {
                Thread.sleep(4800);
            } catch (InterruptedException e) {}
            hardFault(2);
        }).start();
    }
}
