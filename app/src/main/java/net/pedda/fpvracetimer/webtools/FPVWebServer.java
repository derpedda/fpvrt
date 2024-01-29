package net.pedda.fpvracetimer.webtools;

import android.content.Context;
import android.util.Log;

import java.io.IOException;


/**
 * Webserver-Class to handle all the hosted webpage stuff in the background
 */
public class FPVWebServer {

    private static final String TAG = "FPVRT-WS";
    static boolean running = false;

    Context ctx;

    FPVWebServerNano nanoserver;

    void runWebserver() {
        if(running) {
            Log.e(TAG, "runWebserver: Only one instance is allowed");
        }
        if(nanoserver == null)
            nanoserver = new FPVWebServerNano(8080, this.ctx);
        try {
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

    public static boolean isRunning() {
        return running;
    }

    public FPVWebServer(Context ctx) {
        this.ctx = ctx;
    }
}
