package com.emmanuelcorrales.locationspoofer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


public class SpooferService extends Service {

    public class SpooferBinder extends Binder {
        public SpooferService getService() {
            return SpooferService.this;
        }
    }

    private static final String TAG = SpooferService.class.getSimpleName();

    private Binder mBinder = new SpooferBinder();
    private LocationSpoofer mSpoofer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        mSpoofer = new LocationSpoofer(this);
        mSpoofer.initializeGpsSpoofing();
        return mBinder;
    }

    public void spoof(LatLng latLng) {
        mSpoofer.mockLocation(latLng);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
