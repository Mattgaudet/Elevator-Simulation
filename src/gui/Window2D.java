package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Window2D extends JFrame {

    private BufferedImage image;
    private int width;
    private int height;

    public Window2D(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D graphics2D = image.createGraphics();
        super.paint(graphics2D);
        graphics2D.dispose();
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        int size = Math.min(w, h);
        graphics.drawImage(image, 0, 0, size, size, null);
    }
}