package com.emmanuelcorrales.locationspoofer.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.emmanuelcorrales.locationspoofer.BuildConfig;

public class MockLocationUtils {

    private static final String TAG = MockLocationUtils.class.getSimpleName();

    public static boolean isMockLocationEnabled(Context context) {
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

    public static String getBestProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, false);
        if (provider == null) {
            Log.e(TAG, "No location provider found!");
        }
        return provider;
    }
}
