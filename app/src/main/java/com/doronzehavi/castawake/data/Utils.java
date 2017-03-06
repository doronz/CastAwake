package com.doronzehavi.castawake.data;

import android.os.Build;

/**
 * Created by D on 9/29/2015.
 */
public class Utils {

    /**
     * Returns whether the SDK is KitKat or later
     */
    public static boolean isKitKatOrLater() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isAPI18OrLater() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

}
