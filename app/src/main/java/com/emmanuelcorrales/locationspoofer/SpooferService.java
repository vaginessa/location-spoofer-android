package com.emmanuelcorrales.locationspoofer;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.emmanuelcorrales.locationspoofer.utils.IntentUtils;
import com.google.android.gms.maps.model.LatLng;


public class SpooferService extends Service implements Runnable {

    public class SpooferBinder extends Binder {
        public SpooferService getService() {
            return SpooferService.this;
        }
    }

    public static final String ACTION_STOP = "stop";

    private static final String TAG = SpooferService.class.getSimpleName();
    private static final int NOTIFICATION_ID = SpooferService.class.hashCode();
    private static final int DELAY_TIME = 10000; //10 seconds

    private Binder mBinder = new SpooferBinder();
    private LocationSpoofer mSpoofer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            stopSpoofing();
        }

        if (mSpoofer == null) {
            mSpoofer = new LocationSpoofer(this);
            mSpoofer.initializeGpsSpoofing();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopSpoofing();
        super.onDestroy();
    }

    @Override
    public void run() {
        while (mSpoofer != null && mSpoofer.getLatLng() != null) {
            mSpoofer.mockLocation(mSpoofer.getLatLng());
            try {
                Thread.sleep(DELAY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public LatLng getSpoofedLocation() {
        if (mSpoofer == null) {
            return null;
        }
        return mSpoofer.getLatLng();
    }

    public void startSpoofing(LatLng latLng) {
        if (mSpoofer == null) {
            Log.d(TAG, "mSpoofer is null");
            return;
        }
        mSpoofer.mockLocation(latLng);
        startForeground(NOTIFICATION_ID, createNotification());
        new Thread(this).start();
    }

    public void stopSpoofing() {
        Log.d(TAG, "stopSpoofing");
        if (mSpoofer != null) {
            mSpoofer.stop();
            mSpoofer = null;
        }
        stopForeground(true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_STOP));
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_spoofing)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setContentIntent(IntentUtils.createAppPendingIntent(this))
                .addAction(android.R.drawable.ic_lock_power_off,
                        getString(R.string.notification_stop_spoofing),
                        IntentUtils.createStopPendingIntent(this))
                .build();
    }
}
