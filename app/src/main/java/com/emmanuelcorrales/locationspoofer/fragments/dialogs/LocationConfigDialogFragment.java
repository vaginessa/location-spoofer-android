package com.emmanuelcorrales.locationspoofer.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.emmanuelcorrales.locationspoofer.R;

public class LocationConfigDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    public static final String TAG = LocationConfigDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.enable_location_dialog)
                .setPositiveButton(R.string.location_settings, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Bug fix for crashing on Samsung S3 Android 4.3.
        //Check if fragment is attached to an Activity.
        if (getActivity() == null) {
            return;
        }
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
}
