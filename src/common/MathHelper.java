package common;

public class MathHelper {
    public static float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }
}
