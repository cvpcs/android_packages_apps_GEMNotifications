package org.cvpcs.android.gemnotifications.utils;

import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GEMInfo {
    public static final String NAME = getSystemProp("ro.cvpcs.build.name", "Unknown");
    public static final String VERSION = getSystemProp("ro.cvpcs.build.version", "0.0.0");
    public static final String PATCH = getSystemProp("ro.cvpcs.build.patch", "0");
    public static final String DEVICE = getSystemProp("ro.product.device", "unknown");
    public static final String STRING = NAME + "-" + VERSION + "/" + PATCH;
    public static final String DEVICE_ID = getDeviceId();
    
    private static final String TAG = "GEMNotifications-GEMInfo";

    private static String getSystemProp(String key, String def) {
        String tmp = SystemProperties.get(key);
        if(tmp.equals("")) {
            tmp = def;
        }
        return tmp;
    }
    
    /**
     * This returns a valid, unique device ID for this phone, encrypted using the
     * built-in public key.  The device id is either an MEID (for CDMA phones) or
     * the IMEI (for GSM phones), with either CDMA: or GSM: prepended to them to
     * differentiate the two and guarantee no collisions.  We only return the encrypted
     * string for privacy reasons, and use that as our identifier, since the encryption
     * method is 1-to-1.
     */
    private static String getDeviceId() {
    	// first we get a telephony manager
    	TelephonyManager tm = TelephonyManager.getDefault();
    	
    	// next we get a string representation of our network
    	String type;
    	switch(tm.getPhoneType()) {
    		case TelephonyManager.PHONE_TYPE_CDMA:
    			type = "cdma";
    			break;
    		case TelephonyManager.PHONE_TYPE_GSM:
    			type = "gsm";
    			break;
    		case TelephonyManager.PHONE_TYPE_NONE:
    			type = "none";
    			break;
    		default:
    			type = "unknown";
    			break;
    	}
    	
    	// this should get us either the MEID or IMEI depending on phone type, as well
    	// as the name
    	String device_id = type + ":" + tm.getDeviceId();
    	
    	// set a default for our encrypted id
    	String enc_id = "unknown";
    	
    	try {
    		// attempt to encrypt the device id
    		enc_id = RSAUtil.encrypt(device_id);
    	} catch(Exception e) {
    		Log.e(TAG, "Error encrypting Device ID", e);
    	}
    	
    	Log.i(TAG, "Encrypted device id: " + enc_id);
    	
    	// return
    	return enc_id;
    }
}
