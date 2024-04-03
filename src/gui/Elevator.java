package gui;

import common.Math;
import common.Config;
import java.awt.Container;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Elevator {

    private CompletableFuture<Void> future;
    private Resource room;
    private Resource door1;
    private Resource door2;

    private static final int loadUnloadResolution = 100;
    private int roomWidth;
    private int roomHeight;
    private int doorWidth;
    private int doorHeight;

    public Elevator(Container container, int x, int y) {
        roomWidth = ResourceLoader.getWidth(ResourceType.ELEVATOR_ROOM);
        roomHeight = ResourceLoader.getHeight(ResourceType.ELEVATOR_ROOM);
        doorWidth = ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR);
        doorHeight = ResourceLoader.getHeight(ResourceType.ELEVATOR_DOOR);

        room = new Resource(ResourceType.ELEVATOR_ROOM, x, y);
        door1 = new Resource(ResourceType.ELEVATOR_DOOR, x, y);
        door2 = new Resource(ResourceType.ELEVATOR_DOOR, x + doorWidth, y);
        container.add(door1);
        container.add(door2);
        container.add(room);
    }

    public synchronized void loadUnload(int passengers) {
        if (future != null) {
            return;
        }

        future = CompletableFuture.runAsync(() -> {
            try {
                int i;
                for (i = 0; i <= Config.LOAD_TIME; i += loadUnloadResolution) {
                    TimeUnit.MILLISECONDS.sleep(loadUnloadResolution);

                    float alpha = (float) i / (float) Config.LOAD_TIME;
                    door1.offset((int) Math.lerp(0, -doorWidth, alpha), 0);
                    door2.offset((int) Math.lerp(0, +doorWidth, alpha), 0);
                }
                TimeUnit.MILLISECONDS.sleep(passengers * Config.LOAD_TIME);
                for (i = 0; i <= Config.LOAD_TIME; i += loadUnloadResolution) {
                    TimeUnit.MILLISECONDS.sleep(loadUnloadResolution);

                    float alpha = 1 - (float) i / (float) Config.LOAD_TIME;
                    door1.offset((int) Math.lerp(0, -doorWidth, alpha), 0);
                    door2.offset((int) Math.lerp(0, +doorWidth, alpha), 0);
                }
            } catch (InterruptedException e) {} finally {
                future = null;
            }
        }).thenRun(() -> {

        });
    }
}
