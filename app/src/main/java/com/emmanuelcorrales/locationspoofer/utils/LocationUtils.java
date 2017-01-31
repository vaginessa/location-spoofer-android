package com.emmanuelcorrales.locationspoofer.utils;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;

public class LocationUtils {

    private LocationUtils() {

    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isGpnOn(Context context) {
        String allowedLocationProviders = Settings.System.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (allowedLocationProviders == null) {
            allowedLocationProviders = "";
        }
        return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
    }
}
