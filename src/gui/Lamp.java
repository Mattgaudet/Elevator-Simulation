package gui;

import java.awt.Color;

/**
 * 
 */
public class Lamp extends Resource {
    /** */
    private int counter;

    /**
     * 
     * @param x
     * @param y
     */
    public Lamp(int x, int y) {
        super(ResourceType.LAMP, x, y);
    }

    /**
     * 
     * @param incoming
     */
    public void notify(boolean incoming) {
        counter += incoming == true ? 1 : -1;
        if (counter == 0) {
            setTint(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        } else {
            setTint(new Color(0.0f, 1.0f, 0.0f, 1.0f));

        }
    }
}
