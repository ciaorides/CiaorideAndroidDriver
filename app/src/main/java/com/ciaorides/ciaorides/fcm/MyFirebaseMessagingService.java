package com.ciaorides.ciaorides.fcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ciaorides.ciaorides.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.MAIN_PREF,MODE_PRIVATE);
        preferences.edit().putString(Constants.FCM_TOKEN,token).apply();
        Log.d(TAG, "Fetching FCM registration token "+token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> payload = remoteMessage.getData();
        Bundle bundle = new Bundle();
        for (String key : payload.keySet()) {
            bundle.putString(key, payload.get(key));
        }

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent localIntent = new Intent(Constants.FCM_TOKEN);
        localBroadcastManager.sendBroadcast(localIntent);

       // ReceivedMessage message = PushMessageBundleHelper.parse(bundle);
       // KiiUser sender = message.getSender();
        /*PushMessageBundleHelper.MessageType type = message.pushMessageType();
        switch (type) {
            case PUSH_TO_APP:
                PushToAppMessage appMsg = (PushToAppMessage)message;
                Log.d(TAG, "PUSH_TO_APP Received");
                break;
            case PUSH_TO_USER:
                PushToUserMessage userMsg = (PushToUserMessage)message;
                Log.d(TAG, "PUSH_TO_USER Received");
                break;
            case DIRECT_PUSH:
                DirectPushMessage directMsg = (DirectPushMessage)message;
                Log.d(TAG, "DIRECT_PUSH Received");
                break;
        }*/
    }
}