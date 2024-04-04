package gui;

import java.awt.Font;
import java.awt.Graphics;

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
        setSize(width + 16, height + 44);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
