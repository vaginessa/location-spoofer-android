package com.emmanuelcorrales.locationspoofer.fragments.dialogs;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.emmanuelcorrales.locationspoofer.R;

public class MockConfigDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    public static final String TAG = MockConfigDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.enable_mock_location_dialog)
                .setPositiveButton(R.string.settings, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), 0);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Cannot open Developer Options.");
            Toast.makeText(getActivity(), "Cannot open Developer Options.", Toast.LENGTH_SHORT).show();
        }
    }
}
