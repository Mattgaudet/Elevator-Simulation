package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

public class ResourceHelper extends JComponent {
    /** */
    protected Rectangle coords = new Rectangle();
    /** */
    protected Dimension offset = new Dimension();
    /** */
    protected Dimension clip = new Dimension();
    /** */
    protected Color tint;    

    public ResourceHelper(int x, int y) {
        coords.x = x;
        coords.y = y;
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

    protected Rectangle getRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.x = coords.x + offset.width + clip.width;
        rectangle.y = GUI.HEIGHT - coords.y - offset.height - coords.height;
        rectangle.width = coords.width - clip.width - clip.height;
        rectangle.height = coords.height;
        return rectangle;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle rectangle = getRectangle();
        graphics.setColor(tint);
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
}
