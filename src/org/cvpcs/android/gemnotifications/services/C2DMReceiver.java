package org.cvpcs.android.gemnotifications.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.cvpcs.android.gemnotifications.R;

import org.cvpcs.android.gemnotifications.activities.GEMNotifications;
import org.cvpcs.android.gemnotifications.utils.DeviceRegistrar;
import org.cvpcs.android.gemnotifications.utils.Preferences;

import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMReceiver extends C2DMBaseReceiver {
    private static final String TAG = "GEMNotifications-C2DMReceiver";

    public C2DMReceiver() {
        super(DeviceRegistrar.SENDER_ID);
    }

    @Override
    public void onRegistered(Context context, String registration) {
        DeviceRegistrar.registerWithServer(context, registration);
    }

    @Override
    public void onUnregistered(Context context) {
        SharedPreferences prefs = Preferences.get(context);
        String deviceRegistrationID = prefs.getString(Preferences.REGISTRATION_KEY, null);
        DeviceRegistrar.unregisterWithServer(context, deviceRegistrationID);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.e(TAG, "Error: " + errorId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String type = extras.getString("type");

        Log.d(TAG, "Got C2DM Message");

        sendNotification(context, type);
    }

    private void sendNotification(Context context, String type) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification
        CharSequence tickerText = getString(R.string.notif_update_ticker);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.app_icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Define expanded view
        CharSequence contentTitle = getString(R.string.notif_update_title);
        CharSequence contentText = getString(R.string.notif_update_long);
        Intent notificationIntent = new Intent(context, GEMNotifications.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        // Fire notification
        nm.notify(0, notification);
    }
}
