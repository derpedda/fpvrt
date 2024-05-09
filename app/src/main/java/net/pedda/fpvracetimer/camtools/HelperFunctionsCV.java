package net.pedda.fpvracetimer.camtools;

import static android.graphics.Color.colorToHSV;

import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.Core.max;

import android.graphics.Color;
import android.util.Log;

import androidx.appcompat.widget.ThemedSpinnerAdapter;

import net.pedda.fpvracetimer.db.FPVDb;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelperFunctionsCV {

    private static final String TAG = "HelperFunctionsCV";

    public static boolean isInitialized = false;

    public static class COLORRANGES {
        public static final Scalar[] RED = get_color_limits(Color.RED);
        public static final Scalar[] BLUE = get_color_limits(Color.BLUE);
        public static final Scalar[] GREEN = get_color_limits(Color.GREEN);
//        public static final Scalar[] WHITE = get_color_limits(Color.valueOf(255, 255, 255, 255).toArgb());
        public static final Scalar[] MAGENTA = get_color_limits(Color.valueOf(255, 0, 255, 255).toArgb());
        public static final Scalar[] CYAN = get_color_limits(Color.CYAN);
        public static final Scalar[] YELLOW = get_color_limits(Color.YELLOW);

        static {
            HelperFunctionsCV.init();
        }
    }

    public static void init() {
        HelperFunctionsCV.mDetectableColors = new HashMap<>();
        mDetectableColors.put("RED", COLORRANGES.RED);
        mDetectableColors.put("BLUE", COLORRANGES.BLUE);
        mDetectableColors.put("GREEN", COLORRANGES.GREEN);
//            mDetectableColors.put("WHITE", COLORRANGES.WHITE);
        mDetectableColors.put("MAGENTA", COLORRANGES.MAGENTA);
        mDetectableColors.put("CYAN", COLORRANGES.CYAN);
        mDetectableColors.put("YELLOW", COLORRANGES.YELLOW);
        isInitialized = true;
        return;
    }

    public static HashMap<String, Scalar[]> mDetectableColors;

    public static String Longcolor2ColorName(long color) {

        Color col = Color.valueOf(color);
        return color2ColorName(col.toArgb());
    }
    public static String color2ColorName(int color) {

        double[] colHSV = cvtColHSV(color).val;

        Scalar colHSVS = cvtColHSV(color);

        for (String k : mDetectableColors.keySet()) {
            Scalar[] ranges = mDetectableColors.get(k);

            double[] min = ranges[0].val;
            double[] max = ranges[1].val;

//            Mat dst;
//            Mat src = new Mat();
//            Mat.zeros(1,1,Imgproc.CV_TYPE
//            inRange(new Mat([[colHSVS]]), ranges[0], ranges[1], dst);

            if( ((min[0] <= colHSV[0]) && ( colHSV[0] <= max[0]))
                    && ((min[1] <= colHSV[1]) && ( colHSV[1] <= max[1]))
                    && ((min[2] <= colHSV[2]) && ( colHSV[2] <= max[2])) ) {
                // we are in range, yay
                return k;
            }
        }
        return null; //NotSupported, make it crash
    }

    private static Scalar cvtColHSV(int color) {
        int b = Color.blue(color);
        int g = Color.green(color);
        int r = Color.red(color);

        // We have a bug, lets test if swapping channels work

        b = b / 255;
        g = g / 255;
        r = r / 255;

        int[] bgr = {b, g, r};

        OptionalInt asMax = Arrays.stream(bgr).max();
        int v = asMax.getAsInt();


        int s = 0;
        int min = Arrays.stream(bgr).min().getAsInt();
        if (v != 0)
            s = (v - (min)) / v;

        int h = 0;

        if (v - min != 0) {
            if (v == r) {
                h = 60 * (g - b) / (v - min);
            } else if (v == g) {
                h = 120 + 60 * (b - r) / (v - min);
            } else if (v == b) {
                h = 240 + 60 * (r - g) / (v - min);
            }
        }

        if (h < 0)
            h = h + 360;

        v = Math.round(((float) 255.0) * v);
        s = Math.round(((float) 255) * s);
        h = Math.round(((float) h) / 2);

        return new Scalar(h, s, v);
    }


    public static Scalar[] get_color_limits(int color) {

        float[] mHSV = new float[3];

        colorToHSV(color, mHSV); // Android: Hue 0..360 vs openCV Hue 0..180

        Scalar scalHSV = cvtColHSV(color);

//        int hue = (int) mHSV[0]/2;
        int hue = (int) scalHSV.val[0];

        Log.d(TAG, String.format("get_color_limits: %f, %f, %f", mHSV[0], mHSV[1], mHSV[2]));

        Scalar lowerLimit;
        Scalar upperLimit;

        if (hue >= 165) {
            lowerLimit = new Scalar(hue - 10, 100, 100);
            upperLimit = new Scalar(180, 255, 255);
        } else if (hue <= 15) {
            lowerLimit = new Scalar(0, 100, 100);
            upperLimit = new Scalar(hue + 10, 255, 255);
        } else {
            lowerLimit = new Scalar(hue - 10, 100, 100);
            upperLimit = new Scalar(hue + 10, 255, 255);
        }

        return new Scalar[]{lowerLimit, upperLimit};
    }

    public static Mat getColorImage(Mat imga, Scalar[] colorranges) {
        Mat img = imga.clone();
        Mat imgHSV = new Mat();
        Imgproc.cvtColor(img, imgHSV, Imgproc.COLOR_BGR2HSV);
        Mat imgGray = new Mat();
        org.opencv.core.Core.inRange(imgHSV, colorranges[0], colorranges[1], imgGray);
        Mat imgRGBA = new Mat();
        Imgproc.cvtColor(imgGray, imgRGBA, Imgproc.COLOR_GRAY2BGR);
        img.release();
        imgHSV.release();
        return imgRGBA;
    }

    public static Mat getColorMask(Mat imga, Scalar[] colorranges) {
        Mat img = imga.clone();
        Mat imgHSV = new Mat();
        Imgproc.cvtColor(img, imgHSV, Imgproc.COLOR_RGB2HSV); // android does bullshit, it is not BGR as expected, but RGB
        Mat imgGray = new Mat();
        org.opencv.core.Core.inRange(imgHSV, colorranges[0], colorranges[1], imgGray);
        img.release();
        imgHSV.release();
        return imgGray;
    }

    public static void gray2BGR(Mat img) {
        Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGR);
    }

    public static void BGR2gray(Mat img) {
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
    }

    public static void BGR2BGRA(Mat img) {
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2BGRA);
    }


    public static void drawVerticalLine(Mat img) {

        Point p1 = new Point(0, img.size(1) / 2.0);
        Point p2 = new Point(img.size(0), img.size(1) / 2.0);
        Imgproc.line(img, p1, p2, new Scalar(255, 0, 0));
    }

    protected static Range[] getDetectionBoundaries(Size size) {
        return new Range[]{
                new Range(0, (int) size.height),
                new Range(((int) (size.width / 2.0) - 100), (int) (size.width / 2.0) + 100)};
    }

    public static Mat getDetectionSubarea(Mat img) {
        return img.submat(getDetectionBoundaries(img.size()));
    }

    public static void drawDetectionRectangle(Mat img) {
        Point p1 = new Point(0, (img.size(1) / 2.0) - 100);
        Point p2 = new Point(img.size(0), (img.size(1) / 2.0 + 100));
        Imgproc.rectangle(img, p1, p2, new Scalar(255, 0, 0));
    }

    public static List<String> detectColors(Mat area) {

        List<String> lColors = new ArrayList<>();
        if(!HelperFunctionsCV.isInitialized)
            return lColors;

        Mat ranged = new Mat();
        long THRESH = 1000;
        for (Map.Entry<String, Scalar[]> c : HelperFunctionsCV.mDetectableColors.entrySet()) {
            // 1. perform inrange thresholding
            Scalar min = c.getValue()[0];
            Scalar max = c.getValue()[1];

            //convert to HSV space... forgot that
            Mat regionHSV = new Mat();
            Imgproc.cvtColor(area, regionHSV, Imgproc.COLOR_RGB2HSV); // Android does bullshit, it is not BGR as expected
            Core.inRange(regionHSV, min, max, ranged);
            // 2. count color-ful pixels
            long count = countNonZero(ranged);
            // 3. add color if over threshold
            if (count > THRESH)
                lColors.add(c.getKey());
        }
        ranged.release();

        return lColors;

    }

    public static void drawColorText(Mat img, String s) {
        Imgproc.putText(img, s, new Point ( 100, img.size(1)/2), 1, 1, new Scalar(255,255,255));
    }


}
