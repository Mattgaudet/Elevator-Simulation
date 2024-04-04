package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * 
 */
public class Resource extends ResourceHelper {
    /** */
    private BufferedImage resource;

    /**
     * 
     * @param type
     * @param x
     * @param y
     */
    public Resource(ResourceType type, int x, int y) {
        super(x, y);
        resource = ResourceLoader.getResource(type);
        coords.width = ResourceLoader.getWidth(type);
        coords.height = ResourceLoader.getHeight(type);
    }

    /**
     * 
     * @return
     */
    private BufferedImage getResource() {
        if (clip.width == 0 && clip.height == 0) {
            return resource;
        }
        int x1 = clip.width;
        int x2 = resource.getWidth() - clip.width - clip.height;
        int y1 = 0;
        int y2 = resource.getHeight();
        try {
            return resource.getSubimage(x1, y1, x2, y2);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 
     * @param graphics
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        BufferedImage resource = getResource();
        if (resource == null) {
            return;
        }
        Rectangle rectangle = getRectangle();
        graphics.drawImage(resource, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this);
        super.paintComponent(graphics);
    }
}
