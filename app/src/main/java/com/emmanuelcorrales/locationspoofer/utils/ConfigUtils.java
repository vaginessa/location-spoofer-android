package com.emmanuelcorrales.locationspoofer.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtils {

    public static String KEY_SHOW_MAP_HINT = "show_map_hint";

    private ConfigUtils() {

    }

    public static void setMapHintVisibility(Context context, boolean visible) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(KEY_SHOW_MAP_HINT, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(KEY_SHOW_MAP_HINT, visible).apply();
    }

    public static boolean isMapHintVisible(Context context) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(KEY_SHOW_MAP_HINT, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_SHOW_MAP_HINT, true);
    }
}
