package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class Resource extends JComponent {

    private BufferedImage resource;
    private Rectangle base = new Rectangle();
    private Rectangle screen = new Rectangle();
    private Dimension bounds = new Dimension();
    private Dimension offset = new Dimension();

    public Resource(ResourceType type, int x, int y) {
        resource = ResourceLoader.getResource(type);
        base.x = x;
        base.y = y;
        base.width = ResourceLoader.getWidth(type);
        base.height = ResourceLoader.getHeight(type);
    }

    public void offset(int x, int y) {
        offset.width = x;
        offset.height = y;
        update();
    }

    public void resize(int x, int y) {
        bounds.width = x;
        bounds.height = y;
        update();
    }

    private void update() {
        float scale = Math.min(bounds.width, bounds.height);
        float x = base.x + offset.width;
        float y = base.y + offset.height;
        float width = base.width;
        float height = base.height;
        screen.x = (int) (x / GUI.width * scale);
        screen.y = (int) (y / GUI.height * scale);
        screen.width = (int) (width / GUI.width * scale);
        screen.height = (int) (height / GUI.height * scale);
        setBounds(0, 0, bounds.width, bounds.height);
        GUI.update();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int x = screen.x;
        int y = bounds.height - screen.y - screen.height;
        int width = screen.width;
        int height = screen.height;
        graphics.drawImage(resource, x, y, width, height, this);
    }
}
