package org.cvpcs.android.gemnotifications.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.cvpcs.android.gemnotifications.activity.GEMNotifications;

public class DeviceRegistrar {
    public static final String SENDER_ID = "gem.c2dm@gmail.com";
    public static final String STATUS_EXTRA = "Status";
    public static final int REGISTERED_STATUS = 1;
    public static final int UNREGISTERED_STATUS = 2;

    private static final String TAG = "GEMNotifications-DeviceRegistrar";

    public static void registerWithServer(final Context context, final String deviceRegistrationID) {
        new Thread(new Runnable() {
            public void run() {
                Intent updateUIIntent = new Intent(GEMNotifications.UPDATE_UI_ACTION);

                try {
                    HttpResponse res = makeRegisterRequest(context, deviceRegistrationID);
                    if (res.getStatusLine().getStatusCode() == 200) {
                        saveDeviceRegistration(context, deviceRegistrationID);
                        saveGEM(context);
                        updateUIIntent.putExtra(STATUS_EXTRA, REGISTERED_STATUS);
                        context.sendBroadcast(updateUIIntent);
                    } else {
                        Log.d(TAG, "Registration Error: " + String.valueOf(res.getStatusLine().getStatusCode()));
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception", e);
                }
            }
        }).start();
    }

    public static void unregisterWithServer(final Context context, final String deviceRegistrationID) {
        new Thread(new Runnable() {
            public void run() {
                Intent updateUIIntent = new Intent(GEMNotifications.UPDATE_UI_ACTION);

                try {
                    HttpResponse res = makeUnregisterRequest(context, deviceRegistrationID);
                    if (res.getStatusLine().getStatusCode() == 200) {
                        removeDeviceRegistration(context);
                        removeGEM(context);
                        updateUIIntent.putExtra(STATUS_EXTRA, UNREGISTERED_STATUS);
                        context.sendBroadcast(updateUIIntent);
                    } else {
                        Log.d(TAG, "Unregistration Error: " + String.valueOf(res.getStatusLine().getStatusCode()));
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception", e);
                }
            }
        }).start();
    }

    private static void saveDeviceRegistration(Context context, String deviceRegistrationID) {
        SharedPreferences.Editor editor = Preferences.get(context).edit();
        editor.putString(Preferences.REGISTRATION_KEY, deviceRegistrationID);
        editor.commit();

        Log.d(TAG, "Saved deviceRegistrationID=" + deviceRegistrationID);
    }
    
    private static void removeDeviceRegistration(Context context) {
        SharedPreferences.Editor editor = Preferences.get(context).edit();
        editor.remove(Preferences.REGISTRATION_KEY);
        editor.commit();

        Log.d(TAG, "Removed deviceRegistrationID");
    }
    
    private static void saveGEM(Context context) {
        SharedPreferences.Editor editor = Preferences.get(context).edit();
        editor.putString(Preferences.GEM_KEY, GEMInfo.STRING);
        editor.commit();

        Log.d(TAG, "Saved buildType=" + GEMInfo.STRING);
    }
    
    private static void removeGEM(Context context) {
        SharedPreferences.Editor editor = Preferences.get(context).edit();
        editor.remove(Preferences.GEM_KEY);
        editor.commit();

        Log.d(TAG, "Removed buildType");
    }
    

    private static HttpResponse makeRegisterRequest(Context context, String deviceRegistrationID) throws Exception {
        List<NameValuePair> params = new ArrayList<NameValuePair>(6);
        params.add(new BasicNameValuePair("registration_id", deviceRegistrationID));
        params.add(new BasicNameValuePair("gem_name", GEMInfo.NAME));
        params.add(new BasicNameValuePair("gem_version", GEMInfo.VERSION));
        params.add(new BasicNameValuePair("gem_patch", GEMInfo.PATCH));
        params.add(new BasicNameValuePair("device", GEMInfo.DEVICE));
        params.add(new BasicNameValuePair("device_id", GEMInfo.DEVICE_ID));

        RegistrationClient client = new RegistrationClient();
        return client.registerRequest(params);
    }

    private static HttpResponse makeUnregisterRequest(Context context, String deviceRegistrationID) throws Exception {
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("device_id", GEMInfo.DEVICE_ID));

        RegistrationClient client = new RegistrationClient();
        return client.unregisterRequest(params);
    }
}
