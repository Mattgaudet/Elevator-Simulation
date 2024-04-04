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
public class Resource extends JComponent {
    /** */
    private BufferedImage resource;
    /** */
    private Rectangle coords = new Rectangle();
    /** */
    private Dimension offset = new Dimension();
    /** */
    private Dimension clip = new Dimension();
    /** */
    private Color tint;

    /**
     * 
     * @param type
     * @param x
     * @param y
     */
    public Resource(ResourceType type, int x, int y) {
        resource = ResourceLoader.getResource(type);
        coords.x = x;
        coords.y = y;
        coords.width = ResourceLoader.getWidth(type);
        coords.height = ResourceLoader.getHeight(type);
        setBounds(0, 0, GUI.WIDTH, GUI.HEIGHT);
        setTint(new Color(0.0f, 0.0f, 0.0f, 0.0f));
    }

    /**
     * 
     * @param x
     */
    public void setOffsetX(int x) {
        offset.width = x;
        GUI.update();
    }

    /**
     * 
     * @param y
     */
    public void setOffsetY(int y) {
        offset.height = y;
        GUI.update();
    }

    /**
     * 
     * @param x
     */
    public void setLeftClip(int x) {
        clip.width = x;
        GUI.update();
    }

    /**
     * 
     * @param x
     */
    public void setRightClip(int x) {
        clip.height = x;
        GUI.update();
    }

    /**
     * 
     * @param tint
     */
    public void setTint(Color tint) {
        this.tint = tint;
        GUI.update();
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
        super.paintComponent(graphics);
        int x = coords.x + offset.width + clip.width;
        int y = GUI.HEIGHT - coords.y - offset.height - coords.height;
        int width = coords.width - clip.width - clip.height;
        int height = coords.height;
        graphics.drawImage(resource, x, y, width, height, this);
        graphics.setColor(tint);
        graphics.fillRect(x, y, width, height);
    }
}
