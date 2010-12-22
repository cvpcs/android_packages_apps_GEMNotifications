package org.cvpcs.android.gemnotifications.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

        // we assume we only have 1 type right now, and we just pass a url to the page we want to direct them to
        int type = 0;
        try {
            type = Integer.parseInt(extras.getString("t"));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing notification type", e);
        }

        String url = extras.getString("u");

        Log.d(TAG, "Got C2DM Message");

        sendNotification(context, type, url);
    }

    private void sendNotification(Context context, int type, String url) {
        SharedPreferences prefs = Preferences.get(context);
        int notifPref = prefs.getInt(Preferences.NOTIFICATIONS_KEY, 0);

        // first check if we should even bother.  type should be an integer with exactly 1 bit selected,
        // representing the notification type.  we then logically AND this with our preferences, which
        // are a bitmask of which notifications we want to receive.  If the result is 0, then it means
        // no prefs matched the type, so we return.
        if((type & notifPref) == 0) {
            return;
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = null;

        if((type & Preferences.NOTIFICATIONS_UPDATES) > 0) {
            // Create Notification
            CharSequence tickerText = getString(R.string.notif_update_ticker);
            long when = System.currentTimeMillis();
            notification = new Notification(R.drawable.app_icon, tickerText, when);
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Define expanded view
            CharSequence contentTitle = getString(R.string.notif_update_title);
            CharSequence contentText = getString(R.string.notif_update_long);

            // create our notification view
            Intent notificationIntent;
            try {
                // try to open browser
                notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            } catch(Exception e) {
                // FAIL
                Log.e(TAG, "Error creating intent for browser", e);
                notificationIntent = new Intent(context, GEMNotifications.class);
            }

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        }

        if((type & Preferences.NOTIFICATIONS_PATCHES) > 0) {
            // Create Notification
            CharSequence tickerText = getString(R.string.notif_patch_ticker);
            long when = System.currentTimeMillis();
            notification = new Notification(R.drawable.app_icon, tickerText, when);
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Define expanded view
            CharSequence contentTitle = getString(R.string.notif_patch_title);
            CharSequence contentText = getString(R.string.notif_patch_long);

            // create our notification view
            Intent notificationIntent;
            try {
                // try to open browser
                notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            } catch(Exception e) {
                // FAIL
                Log.e(TAG, "Error creating intent for browser", e);
                notificationIntent = new Intent(context, GEMNotifications.class);
            }

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        }

        if(notification != null) {
            // Fire notification
            nm.notify(0, notification);
        }
    }
}
