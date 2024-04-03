package gui;

import javax.swing.SwingUtilities;

public class Window {

    public static final int FLOORS = 22;
    public static final int ELEVATORS = 4;
    public static final int WIDTH = 704;
    public static final int HEIGHT = 704;
    public static final int OFFSET = 52;

    private static Window2D window;
    private static Elevator[] elevators = new Elevator[ELEVATORS];
    private static Floor[] floors = new Floor[FLOORS];

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            window = new Window2D(WIDTH, HEIGHT);

            int i;
            for (i = 0; i < ELEVATORS; i++) {
                elevators[i] = new Elevator(window, i);
            }
            for (i = 0; i < FLOORS; i++) {
                floors[i] = new Floor(window, i);
            }

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

            update();
        });
    }

    public static void update() {
        window.repaint();
    }

    public static void main(String[] args) {
        init();
    }
}
