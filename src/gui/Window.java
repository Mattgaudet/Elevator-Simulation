package gui;

import javax.swing.JFrame;

/**
 * 
 */
public class Window extends JFrame {

    /**
     * 
     * @param width
     * @param height
     */
    public Window(int width, int height) {
        super();
        setSize(width + 8, height + 48);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
