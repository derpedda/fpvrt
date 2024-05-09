package net.pedda.fpvracetimer.camtools;

import static android.graphics.Color.colorToHSV;
import static android.graphics.Color.green;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class HelperFunctions {

    private static final String TAG = "FPVRTHelperFunctions";


    public static class COLORRANGES {
        static final int[][] RED = get_color_limits(Color.RED);
        static final int[][] BLUE = get_color_limits(Color.BLUE);
        static final int[][] GREEN = get_color_limits(Color.GREEN);
        static final int[][] ORANGE = get_color_limits(Color.valueOf(255,255,0).toArgb());
        static final int[][] PURPLE = get_color_limits(Color.valueOf(255,0,255).toArgb());
        static final int[][] CYAN = get_color_limits(Color.CYAN);
        static final int[][] YELLOW = get_color_limits(Color.YELLOW);
    }


    public static int[][] get_color_limits(int color) {

        float[] mHSV = new float[3];

        colorToHSV(color, mHSV);

        int hue = (int) mHSV[0];

        Log.d(TAG, "get_color_limits: " + hue);

        int[] lowerLimit;
        int[] upperLimit;

        if (hue >= 165) {
            lowerLimit = new int[]{hue - 10, 100, 100};
            upperLimit = new int[]{180, 255, 255};
        } else if (hue <= 15) {
            lowerLimit = new int[]{0, 100, 100};
            upperLimit = new int[]{hue + 10, 255, 255};
        } else {
            lowerLimit = new int[]{hue - 10, 100, 100};
            upperLimit = new int[]{hue + 10, 255, 255};
        }

        return new int[][]{lowerLimit, upperLimit};
    }

    public static int classifyColor(Color c) {

        int ci = c.toArgb();

        // TODO: compare colors to ranges

        int[][] limits = get_color_limits(c.toArgb());

        int[] lowerLimits = limits[0];
        int[] upperLimits = limits[1];

        float[] colorshsv = new float[3];
        colorToHSV(ci, colorshsv);

        int hsv = (int)colorshsv[0];

        // compare hsv only TODO: Make this include DATA

        return 0;
    }

}

