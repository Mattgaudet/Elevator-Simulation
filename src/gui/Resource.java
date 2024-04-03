package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class Resource extends JComponent {

    private BufferedImage resource;
    private Rectangle coords = new Rectangle();
    private Dimension offset = new Dimension();

    public Resource(ResourceType type, int x, int y) {
        resource = ResourceLoader.getResource(type);
        coords.x = x;
        coords.y = y;
        coords.width = ResourceLoader.getWidth(type);
        coords.height = ResourceLoader.getHeight(type);
        setBounds(0, 0, Window.WIDTH, Window.HEIGHT);
    }

    public void setOffsetX(int x) {
        offset.width = x;
        Window.update();
    }

    public void setOffsetY(int y) {
        offset.height = y;
        Window.update();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int x = coords.x + offset.width;
        int y = coords.y + offset.height;
        int width = coords.width;
        int height = coords.height;
        graphics.drawImage(resource, x, y, width, height, this);
    }
}
