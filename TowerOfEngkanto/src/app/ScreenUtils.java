package app;

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenUtils {
    public static final int WIDTH;
    public static final int HEIGHT;

    public static final double SCALE_X;
    public static final double SCALE_Y;

    static {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screen.width;
        HEIGHT = screen.height;
        SCALE_X = WIDTH / 1920.0;
        SCALE_Y = HEIGHT / 1080.0;
    }

    public static int scaleX(int value) {
        return (int) (value * SCALE_X);
    }

    public static int scaleY(int value) {
        return (int) (value * SCALE_Y);
    }

    public static int scaleFont(int size) {
        return (int) (size * Math.min(SCALE_X, SCALE_Y));
    }
}
