package com.weartech.openeyetapcompanion;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Based on code written by mukesh on 19/5/15.
 */


public class NotificationService extends NotificationListenerService {
    private final String TAG = "NotificationService";
    Context context;

    @Override
    public void onCreate() {
        Log.d(TAG, "Notification Service has been initialized");
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "MessageReceived");
        String pack = sbn.getPackageName();
        Bundle extras;
        try {
            extras = sbn.getNotification().extras;
        } catch (Exception e) {
            Log.d(TAG, "Failed to fetch notification");
            return;
        }

        String title = extras.getString("android.title");
        String text = Objects.requireNonNull(extras.getCharSequence("android.text")).toString();
        // int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap img = sbn.getNotification().largeIcon;

        Log.d(TAG, "Package: " + pack);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Text: " + text);

        Intent msgIntent = new Intent("Msg");
        msgIntent.putExtra("package", pack);
        msgIntent.putExtra("title", title);
        msgIntent.putExtra("text", text);

        // TODO make it work with images

        if (img != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgIntent.putExtra("icon", byteArray);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Notification Removed");

    }
}