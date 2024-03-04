package net.pedda.fpvracetimer.db;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.webtools.FPVWebServer;

public class DBService extends Worker {

    static final String TAG = "DBWorker";

    private final NotificationManager notificationManager;

    private final int notificationId = 37075;
    private final Context ctx;

    public DBService(
            @NonNull Context context,
            @NonNull WorkerParameters parameters) {
        super(context, parameters);
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        this.ctx = context;
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        Data inputData = getInputData();
        // Mark the Worker as important
        String progress = "Starting WebServer";
        setForegroundAsync(createForegroundInfo(progress));

        return ListenableWorker.Result.success();
    }

    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        // Build a notification using bytesRead and contentLength

        Context context = getApplicationContext();
        String id = context.getString(R.string.webserver_notification_channel_id);
        String title = context.getString(R.string.webserver_notification_title);
        String cancel = context.getString(R.string.webserver_notification_stop);
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        Notification notification = new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build();

        return new ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createChannel() {
        // Create a Notification channel
        String channelName = "FPVRT-DB";
        String channelId = ctx.getResources().getString(R.string.webserver_notification_channel_id);
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager service = (NotificationManager)ctx.getSystemService(NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
}

