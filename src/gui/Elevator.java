// package gui;

// import common.Math;
// import common.Config;
// import java.awt.Container;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.locks.Lock;
// import java.util.concurrent.locks.ReentrantLock;

// public class Elevator {

//     private Lock lock;
//     private Resource room;
//     private Resource door1;
//     private Resource door2;
//     private Resource rope1;
//     private Resource rope2;

//     public Elevator(Container container, int x) {
//         room = new Resource(ResourceType.ELEVATOR_ROOM, x, 0);
//         door1 = new Resource(ResourceType.ELEVATOR_DOOR, x, 0);
//         door2 = new Resource(ResourceType.ELEVATOR_DOOR, x + ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR), 0);
//         rope1 = new Resource(ResourceType.ELEVATOR_ROPE, x + 1, 0);
//         rope2 = new Resource(ResourceType.ELEVATOR_ROPE, x + ResourceLoader.getWidth(ResourceType.ELEVATOR_ROOM) - 1, 0);
//         container.add(rope1);
//         container.add(rope2);
//         container.add(door1);
//         container.add(door2);
//         container.add(room);

//         lock = new ReentrantLock();
//     }

//     public void action(int passengers, int floor) {
//         new Thread(() -> {
//             if (!lock.tryLock()) {
//                 return;
//             }
//             try {
//                 load(passengers);
//                 // move(floor);
//                 // load(passengers);
//             } finally {
//                 lock.unlock();
//             }
//         }).start();
//     }

//     private void load(int passengers) {
//         int i;
//         int resolution = 100;
//         int end = ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR);
//         for (i = 0; i <= Config.LOAD_TIME; i += resolution) {
//             try {
//                 TimeUnit.MILLISECONDS.sleep(resolution);
//             } catch (InterruptedException e) {}

//             float alpha = (float) i / (float) Config.LOAD_TIME;
//             door1.setOffsetX((int) Math.lerp(0, -end, alpha));
//             door2.setOffsetX((int) Math.lerp(0, +end, alpha));
//         }
//         try {
//             TimeUnit.MILLISECONDS.sleep(passengers * Config.LOAD_TIME);
//         } catch (InterruptedException e) {}
//         for (i = 0; i <= Config.LOAD_TIME; i += resolution) {
//             try {
//                 TimeUnit.MILLISECONDS.sleep(resolution);
//             } catch (InterruptedException e) {}
//             float alpha = 1 - (float) i / (float) Config.LOAD_TIME;
//             door1.setOffsetX((int) Math.lerp(0, -end, alpha));
//             door2.setOffsetX((int) Math.lerp(0, +end, alpha));
//         }
//     }

//     private void move(int floor) {

//     }

//     public static int getWidth() {
//         return ResourceLoader.getWidth(ResourceType.ELEVATOR_DOOR);
//     }

//     public static int getHeight() {
//         return Window.HEIGHT;
//     }
// }
