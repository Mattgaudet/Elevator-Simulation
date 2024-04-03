package gui;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GUI {

    public static final int width = 600;
    public static final int height = 600;
    public static JFrame frame;

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();
            frame.setSize(width, height);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    int w = frame.getContentPane().getWidth();
                    int h = frame.getContentPane().getHeight();
                    for (Component component : frame.getContentPane().getComponents()) {
                        if (!(component instanceof Resource)) {
                            continue;
                        }
                        Resource resource = (Resource) component;
                        resource.resize(w, h);
                    }
                }
            });

            Elevator elevator = new Elevator(frame, 32, 32);
            elevator.loadUnload(1);
        });
    }

    public static void update() {
        frame.repaint();
    }

    public static void main(String[] args) {
        init();
    }
}
