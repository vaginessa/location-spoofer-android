package com.emmanuelcorrales.locationspoofer.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;

import com.emmanuelcorrales.locationspoofer.R;
import com.emmanuelcorrales.locationspoofer.utils.ConfigUtils;

public class MapHintDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String TAG = MapHintDialogFragment.class.getSimpleName();

    private CheckBox mCheckBox;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.map_hint_title)
                .setMessage(R.string.map_hint)
                .setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ConfigUtils.setMapHintVisibility(getActivity(), !mCheckBox.isChecked());
        dismiss();
    }
}
