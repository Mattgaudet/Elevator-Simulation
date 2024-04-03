package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Window2D extends JFrame {

    private BufferedImage image;

    public Window2D(int width, int height) {
        super();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        setSize(width, height + Window.OFFSET);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D graphics2D = image.createGraphics();

        // flip on y axis for bottom left rendering
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -image.getHeight());
        graphics2D.setTransform(transform);

        super.paint(graphics2D);
        graphics2D.dispose();
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        graphics.drawImage(image, 0, Window.OFFSET, size, size, null);
    }
}
