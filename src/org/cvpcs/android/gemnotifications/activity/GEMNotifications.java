package org.cvpcs.android.gemnotifications.activity;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import org.cvpcs.android.gemnotifications.R;

import org.cvpcs.android.gemnotifications.util.DeviceRegistrar;
import org.cvpcs.android.gemnotifications.util.GEMInfo;
import org.cvpcs.android.gemnotifications.util.Preferences;
import com.google.android.c2dm.C2DMessaging;

public class GEMNotifications extends Activity {
    public static final String UPDATE_UI_ACTION = "org.cvpcs.android.gemnotifications.UPDATE_UI";

    private Button mRegisterButton;
    private Button mUnregisterButton;
    private CheckBox mUpdateCheckBox;
    private CheckBox mPatchCheckBox;
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
        
        mUpdateCheckBox = (CheckBox) findViewById(R.id.updates);
        mUpdateCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        
        mPatchCheckBox = (CheckBox) findViewById(R.id.patches);
        mPatchCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);

        checkGEM();
        setup();

        registerReceiver(mUpdateUIReceiver, new IntentFilter(UPDATE_UI_ACTION));
    }

    private void checkGEM() {
        SharedPreferences settings = Preferences.get(this);
        SharedPreferences.Editor editor = settings.edit();
        String gem = settings.getString(Preferences.GEM_KEY, null);
        
        if (gem != null && !gem.equals(GEMInfo.STRING)) {
            Log.d(TAG, "Build Changed -- Removing settings.");
            editor.remove(Preferences.NOTIFICATIONS_KEY);
            editor.remove(Preferences.GEM_KEY);
            editor.remove(Preferences.REGISTRATION_KEY);
            editor.commit();
        }
    }

    private void setup() {
        SharedPreferences settings = Preferences.get(this);
        String deviceRegistrationId = settings.getString(Preferences.REGISTRATION_KEY, null);
        int notifications = settings.getInt(Preferences.NOTIFICATIONS_KEY, 0);
        if (deviceRegistrationId == null) {
            mRegisterButton.setEnabled(true);
            mUnregisterButton.setEnabled(false);
        } else {
            mRegisterButton.setEnabled(false);
            mUnregisterButton.setEnabled(true);
        }
    	mUpdateCheckBox.setChecked((notifications & Preferences.NOTIFICATIONS_UPDATES) > 0);
    	mPatchCheckBox.setChecked((notifications & Preferences.NOTIFICATIONS_PATCHES) > 0);
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
    
    private final OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int notifications = 0;
			
			notifications |= (mUpdateCheckBox.isChecked() ? Preferences.NOTIFICATIONS_UPDATES : 0);
			notifications |= (mPatchCheckBox.isChecked() ? Preferences.NOTIFICATIONS_PATCHES : 0);

	        SharedPreferences settings = Preferences.get(buttonView.getContext());
	        SharedPreferences.Editor editor = settings.edit();
			editor.putInt(Preferences.NOTIFICATIONS_KEY, notifications);
			editor.commit();
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
