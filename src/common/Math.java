package common;

public class Math {
    public static float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }
}
