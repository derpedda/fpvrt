package net.pedda.fpvracetimer.camtools;

import android.content.Context;

public class CameraHandler {


    static CameraHandler initCameraHandler(Context ctx) {

        System.loadLibrary("opencv_java4");
        CameraHandler ch = new CameraHandler();
        return ch;
    }

}
