package org.cvpcs.android.gemnotifications.utils;

import android.os.SystemProperties;

public class GEMInfo {
    public static final String NAME = getSystemProp("ro.cvpcs.build.name", "Unknown");
    public static final String VERSION = getSystemProp("ro.cvpcs.build.version", "0.0.0");
    public static final String PATCH = getSystemProp("ro.cvpcs.build.patch", "0");
    public static final String DEVICE = getSystemProp("ro.product.device", "unknown");
    public static final String STRING = NAME + "-" + VERSION + "/" + PATCH;

    private static String getSystemProp(String key, String def) {
        String tmp = SystemProperties.get(key);
        if(tmp.equals("")) {
            tmp = def;
        }
        return tmp;
    }
}
