package com.emmanuelcorrales.locationspoofer.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.emmanuelcorrales.locationspoofer.LocationSpoofer;
import com.emmanuelcorrales.locationspoofer.R;


public class SpoofDialogFragment extends DialogFragment implements DialogInterface.OnClickListener,
        TextWatcher {

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

    private EditText mLatEt;
    private EditText mLongEt;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_spoof, null);

        mLatEt = (EditText) view.findViewById(R.id.latitude);
        mLatEt.addTextChangedListener(this);

        mLongEt = (EditText) view.findViewById(R.id.longitude);
        mLongEt.addTextChangedListener(this);

        Bundle args = getArguments();
        if (args != null) {
            double latitude = getArguments().getDouble(KEY_LATITUDE);
            mLatEt.setText(String.valueOf(latitude));

            double longitude = getArguments().getDouble(KEY_LONGITUDE);
            mLongEt.setText(String.valueOf(longitude));
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_mock_location)
                .setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        validateInput();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Activity activity = getActivity();
        if (activity == null || mLatEt.length() == 0 || mLongEt.length() == 0) {
            return;
        }

        double latitude = Double.valueOf(mLatEt.getText().toString());
        double longitude = Double.valueOf(mLongEt.getText().toString());

        LocationSpoofer spoofer = new LocationSpoofer(getActivity());
        spoofer.initializeGpsSpoofing();
        spoofer.mockLocation(latitude, longitude);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)
                activity.getWindow().getDecorView().findViewById(R.id.coordinator_layout);

        Snackbar.make(coordinatorLayout,
                "Spoofed location at " + latitude + "," + longitude + ".",
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        validateInput();
    }

    private void validateInput() {
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) {
            return;
        }
        if (mLatEt.getText().length() != 0 && mLongEt.getText().length() != 0) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
