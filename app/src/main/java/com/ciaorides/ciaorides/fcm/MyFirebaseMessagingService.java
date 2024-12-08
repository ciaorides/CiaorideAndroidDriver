package com.ciaorides.ciaorides.fcm;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ciaorides.ciaorides.R;
import com.ciaorides.ciaorides.utils.Constants;
import com.ciaorides.ciaorides.view.activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.MAIN_PREF, MODE_PRIVATE);
        preferences.edit().putString(Constants.FCM_TOKEN, token).apply();
        Log.d(TAG, "Fetching FCM registration token " + token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FirebaseNotification", remoteMessage.getData().toString());
        Map<String, String> payload = remoteMessage.getData();
        Log.d("FirebaseNotification", payload.toString());
        Bundle bundle = new Bundle();
        for (String key : payload.keySet()) {
            bundle.putString(key, payload.get(key));
        }
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent localIntent = new Intent(Constants.FCM_TOKEN);
        localBroadcastManager.sendBroadcast(localIntent);

    }
}

