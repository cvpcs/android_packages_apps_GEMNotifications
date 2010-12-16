package org.cvpcs.android.gemnotifications.utils;

import android.os.SystemProperties;

public class StringUtils {
    public static String getGEM() {
        return SystemProperties.get("ro.cvpcs.build.name");
    }
    
    public static String getGEMVersion() {
    	return SystemProperties.get("ro.cvpcs.build.version");
    }
    
    public static String getGEMPatchVersion() {
    	return SystemProperties.get("ro.cvpcs.build.patchversion");
    }
    
    public static String getModVersion() {
        return SystemProperties.get("ro.modversion");
    }
    
    public static boolean isNewVersion(String oldVersion, String newVersion) {
        newVersion = newVersion.replace(".", "");
        oldVersion = oldVersion.replace(".", "");

        if (Integer.valueOf(newVersion) > Integer.valueOf(oldVersion)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static String getGEMString() {
    	return StringUtils.getGEM() + "-" + StringUtils.getGEMVersion() + "/" + StringUtils.getGEMPatchVersion();
    }
}
