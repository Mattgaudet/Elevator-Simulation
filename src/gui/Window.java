package gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Window {

    public static final int FLOORS = 22;
    public static final int ELEVATORS = 4;
    public static final int WIDTH = 704;
    public static final int HEIGHT = 704;
    public static Window2D window;

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            window = new Window2D(WIDTH, HEIGHT);
            window.setSize(WIDTH, HEIGHT);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);

            // Elevator elevator = new Elevator(window, 32);
            // elevator.action(1, 0);

            Resource resource = new Resource(ResourceType.ELEVATOR_ROOM, 32, 32);
            window.add(resource);
        });
    }

    public static void update() {
        window.repaint();
    }

    public static void main(String[] args) {
        init();
    }
}
