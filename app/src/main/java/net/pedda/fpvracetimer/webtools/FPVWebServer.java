package net.pedda.fpvracetimer.webtools;

import android.util.Log;

import java.io.IOException;


/**
 * Webserver-Class to handle all the hosted webpage stuff in the background
 */
public class FPVWebServer {

    private static final String TAG = "FPVRT-WS";
    static boolean running = false;

    static FPVWebServerNano nanoserver;

    void runWebserver() {
        if(nanoserver != null)
            nanoserver = new FPVWebServerNano(8080);
        try {
            assert nanoserver != null;
            nanoserver.start();
        } catch (IOException ioex) {
            Log.e(TAG, "runWebserver: Unable to start webserver");
        }
        running = true;
    }

    void stopWebserver() {
        if(running) {
            if (nanoserver != null) {
                nanoserver.stop();
                running = false;
            }
        }
    }




}
