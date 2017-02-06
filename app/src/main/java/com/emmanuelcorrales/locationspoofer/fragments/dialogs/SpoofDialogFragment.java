package com.emmanuelcorrales.locationspoofer.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.emmanuelcorrales.locationspoofer.LocationSpoofer;
import com.emmanuelcorrales.locationspoofer.R;


public class SpoofDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String TAG = SpoofDialogFragment.class.getSimpleName();
    public static final String KEY_LATITUDE = "key_latitude";
    public static final String KEY_LONGITUDE = "key_longitude";

    public static SpoofDialogFragment newInstance(double latitude, double longitude) {
        Bundle args = new Bundle();
        args.putDouble(KEY_LATITUDE, latitude);
        args.putDouble(KEY_LONGITUDE, longitude);
        SpoofDialogFragment sdf = new SpoofDialogFragment();
        sdf.setArguments(args);
        return sdf;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_spoof, null);
        Bundle args = getArguments();
        if (args != null) {
            double latitude = getArguments().getDouble(KEY_LATITUDE);
            EditText latitudeEt = (EditText) view.findViewById(R.id.latitude);
            latitudeEt.setText(String.valueOf(latitude));

            double longitude = getArguments().getDouble(KEY_LONGITUDE);
            EditText longitudeEt = (EditText) view.findViewById(R.id.longitude);
            longitudeEt.setText(String.valueOf(longitude));
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_mock_location)
                .setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        LocationSpoofer spoofer = new LocationSpoofer(getActivity());
        spoofer.initializeGpsSpoofing();
        double latitude = getArguments().getDouble(KEY_LATITUDE);
        double longitude = getArguments().getDouble(KEY_LONGITUDE);
        spoofer.mockLocation(latitude, longitude);
    }

}
