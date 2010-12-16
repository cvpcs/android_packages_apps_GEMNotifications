package org.cvpcs.android.gemnotifications.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.cvpcs.android.gemnotifications.R;

import org.cvpcs.android.gemnotifications.utils.DeviceRegistrar;
import org.cvpcs.android.gemnotifications.utils.Preferences;
import org.cvpcs.android.gemnotifications.utils.StringUtils;
import com.google.android.c2dm.C2DMessaging;

public class GEMNotifications extends Activity {
    public static final String UPDATE_UI_ACTION = "org.cvpcs.android.gemnotifications.UPDATE_UI";

    private Button mRegisterButton;
    private Button mUnregisterButton;
    private ProgressDialog mProgressDialog;

    private static final String TAG = "GEMNotifications-MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(R.string.app_name);

        mRegisterButton = (Button) findViewById(R.id.register);
        mRegisterButton.setOnClickListener(mOnRegisterListener);

        mUnregisterButton = (Button) findViewById(R.id.unregister);
        mUnregisterButton.setOnClickListener(mOnUnregisterListener);

        checkGEM();
        setupButtons();

        registerReceiver(mUpdateUIReceiver, new IntentFilter(UPDATE_UI_ACTION));
    }

    private void checkGEM() {
        SharedPreferences settings = Preferences.get(this);
        SharedPreferences.Editor editor = settings.edit();
        String gem = settings.getString(Preferences.GEM_KEY, null);
        
        if (gem != null && !gem.equals(StringUtils.getGEMString())) {
            Log.d(TAG, "Build Changed -- Removing settings.");
            editor.remove(Preferences.NOTIFICATIONS_KEY);
            editor.remove(Preferences.GEM_KEY);
            editor.remove(Preferences.REGISTRATION_KEY);
            editor.commit();
        }
    }

    private void setupButtons() {
        SharedPreferences settings = Preferences.get(this);
        String deviceRegistrationId = settings.getString(Preferences.REGISTRATION_KEY, null);
        if (deviceRegistrationId == null) {
            mRegisterButton.setEnabled(true);
            mUnregisterButton.setEnabled(false);
        } else {
            mRegisterButton.setEnabled(false);
            mUnregisterButton.setEnabled(true);
        }
    }

    private final OnClickListener mOnRegisterListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mProgressDialog = ProgressDialog.show(GEMNotifications.this,
                    "", getString(R.string.registering), true);
            mProgressDialog.show();

            mRegisterButton.setEnabled(false);
            C2DMessaging.register(GEMNotifications.this, DeviceRegistrar.SENDER_ID);
        }
    };

    private final OnClickListener mOnUnregisterListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mProgressDialog = ProgressDialog.show(GEMNotifications.this,
                    "", getString(R.string.unregistering), true);
            mProgressDialog.show();

            mUnregisterButton.setEnabled(false);
            C2DMessaging.unregister(GEMNotifications.this);
        }

    };

    private final BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(DeviceRegistrar.STATUS_EXTRA, 0);

            if (status == DeviceRegistrar.REGISTERED_STATUS) {
                mUnregisterButton.setEnabled(true);
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.registration_success, 1000).show();
            }

            if (status == DeviceRegistrar.UNREGISTERED_STATUS) {
                mRegisterButton.setEnabled(true);
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.unregistration_success, 1000).show();
            }
        }
    };
}
