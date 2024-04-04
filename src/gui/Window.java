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
        setSize(width, height + GUI.OFFSET);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
