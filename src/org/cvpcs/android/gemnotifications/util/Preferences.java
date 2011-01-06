package org.cvpcs.android.gemnotifications.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences {
	public static final String REGISTRATION_KEY = "registration_id";
	public static final String GEM_KEY = "gem";
	public static final String NOTIFICATIONS_KEY = "notifications";
	
	public static final int NOTIFICATIONS_UPDATES = 0x01;
	public static final int NOTIFICATIONS_PATCHES = 0x02;

	public static SharedPreferences get(Context context) {
		return context.getSharedPreferences("GEM_NOTIFICATIONS_PREFS", 0);
	}
}
