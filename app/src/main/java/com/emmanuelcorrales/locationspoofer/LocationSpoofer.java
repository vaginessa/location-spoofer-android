package com.emmanuelcorrales.locationspoofer;

import android.app.AppOpsManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class LocationSpoofer {
    private static final String TAG = LocationSpoofer.class.getSimpleName();
    private static final int DEFAULT_ACCURACY = 1;

    /**
     * Check if mock location is enabled on developer options.
     *
     * @return true if mock location is enabled else it returns false.
     */
    public static boolean canMockLocation(@NonNull Context context) {
        boolean isEnabled = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                isEnabled = opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(),
                        BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED;
            } else {
                return !Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            }
        } catch (Exception e) {
            Log.d(TAG, "Mock location is not enabled.");
        }
        return isEnabled;
    }

    private Context mContext;
    private LocationManager mLocationManager;
    private LatLng mLatLang;

    public LocationSpoofer(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Argument 'context' cannot be null.");
        }
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void initializeGpsSpoofing() {
        if (!canMockLocation(mContext)) {
            Log.e(TAG, "Cannot initialize GPS spoofing. Mock location is not enabled on Developer options.");
            return;
        }
        mLocationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,           //name
                false,                                  //requiresNetwork
                false,                                  //requiresSatellite
                false,                                  //requiresCell
                false,                                  //hasMonetaryCost
                true,                                   //supportsAltitude
                true,                                   //supportsSpeed
                true,                                   //supportsBearing
                0,                                      //powerRequirement
                DEFAULT_ACCURACY                        //accuracy
        );
        mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
    }

    public LatLng getLatLng() {
        return mLatLang;
    }

    public void mockLocation(LatLng latLng) {
        mLatLang = latLng;
        mockLocation(mLatLang.latitude, mLatLang.longitude);
    }

    private void mockLocation(double latitude, double longitude) {
        mockLocation(latitude, longitude, DEFAULT_ACCURACY);
    }

    private void mockLocation(double latitude, double longitude, float accuracy) {
        Location nextLocation = new Location(LocationManager.GPS_PROVIDER);
        nextLocation.setLatitude(latitude);
        nextLocation.setLongitude(longitude);
        nextLocation.setAccuracy(accuracy);
        nextLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            nextLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, nextLocation);
    }
}
