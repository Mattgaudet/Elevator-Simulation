package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * 
 */
public class Number extends ResourceHelper {
    /** */
    private int value;
    /** */
    private int size;
    /** */
    private Color color;

    /**
     * 
     * @param x
     * @param y
     * @param color
     */
    public Number(int x, int y, Color color) {
        super(x, y);
        this.color = color;
        this.size = 12;
    }

    /**
     * 
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
        GUI.update();
    }

    /**
     * 
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
        GUI.update();
    }

    /**
     * 
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * 
     * @param value
     */
    public void addValue(int value) {
        setValue(this.value + value);
    }

    /**
     * 
     * @param value
     */
    public void subValue(int value) {
        setValue(this.value - value);
    }

    /**
     * 
     * @param graphics
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Rectangle rectangle = getRectangle();
        Font font = new Font("Arial", Font.PLAIN, size);
        graphics.setFont(font);
        graphics.setColor(color);
        graphics.drawString(String.valueOf(value), rectangle.x, rectangle.y);
    }
}
