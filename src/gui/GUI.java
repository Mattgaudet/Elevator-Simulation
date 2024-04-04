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
    public static final int WIDTH = 384;
    /** */
    public static final int HEIGHT = 704;
    /** */
    public static final int OFFSET = 64;
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

        elevators[1].move(8);
        elevators[1].open();
        elevators[1].load(1);
        elevators[1].close();

        elevators[3].move(4);
        elevators[3].open();
        elevators[3].load(1);
        elevators[3].close();
        elevators[3].move(2);

        elevators[0].move(20);
        elevators[2].move(19);
        elevators[0].open();
    }
}
