package com.emmanuelcorrales.locationspoofer.utils;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.emmanuelcorrales.locationspoofer.SpooferService;
import com.emmanuelcorrales.locationspoofer.activities.MainActivity;

import static com.emmanuelcorrales.locationspoofer.SpooferService.ACTION_STOP;

public class IntentUtils {

    private IntentUtils() {

    }

    public static PendingIntent createAppPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createStopPendingIntent(Context context) {
        Intent intent = new Intent(context, SpooferService.class);
        intent.setAction(ACTION_STOP);
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
