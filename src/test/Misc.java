package test;

import static org.junit.jupiter.api.Assertions.*;

public class Misc {

    /**
     * Assert a function takes min to max amount of time to execute.
     * @param action The function to assert.
     * @param min The minimum amount of time.
     * @param max The maximum amount of time.
     */
    public static void assertWait(Runnable action, long min, long max) {
        long t1 = System.currentTimeMillis();
        action.run();
        long t2 = System.currentTimeMillis();
        long duration = (t2 - t1) / 1000;
        assertTrue(duration >= min);
        assertTrue(duration <= max);
    }
}
