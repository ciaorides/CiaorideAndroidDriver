package com.ciaorides.ciaorides.fcm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ciaorides.ciaorides.R;

public class OreoNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "Fcm Test";
    private static final String CHANNEL_NAME = "Fcm Test";
    private NotificationManager notificationManager;

    public OreoNotification(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,  NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("New Ride!");
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        channel.setShowBadge(false);
        channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ringtone), audioAttributes);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }


    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon){
        return  new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setFlag(Notification.FLAG_AUTO_CANCEL|Notification.FLAG_INSISTENT, true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setTicker("Ciao Ride!")
                .setNumber(10)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");
    }
}
