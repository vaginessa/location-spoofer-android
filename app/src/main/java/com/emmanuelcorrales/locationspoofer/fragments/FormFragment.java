package com.emmanuelcorrales.locationspoofer.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.emmanuelcorrales.android.utils.EditTextUtils;
import com.emmanuelcorrales.android.utils.KeyboardUtils;
import com.emmanuelcorrales.locationspoofer.R;
import com.emmanuelcorrales.locationspoofer.LocationSpoofer;

public class FormFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = FormFragment.class.getSimpleName();

    public static FormFragment newInstance(LocationSpoofer spoofer) {
        FormFragment fragment = new FormFragment();
        fragment.setSpoofer(spoofer);
        return fragment;
    }

    private LocationSpoofer mSpoofer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mockLocBtn = (Button) getActivity().findViewById(R.id.mock_location);
        mockLocBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        KeyboardUtils.hideKeyboard(getActivity(), getView());

        EditText latitudeEt = (EditText) getView().findViewById(R.id.latitude);
        EditText longitudeEt = (EditText) getView().findViewById(R.id.longitude);
        EditText accuracyEt = (EditText) getView().findViewById(R.id.accuracy);
        String message;

        if (EditTextUtils.validateEmpty(latitudeEt, R.string.validation_required)
                & EditTextUtils.validateEmpty(longitudeEt, R.string.validation_required)
                & EditTextUtils.validateEmpty(accuracyEt, R.string.validation_required)) {
            double latitude = Double.valueOf(latitudeEt.getText().toString());
            double longitude = Double.valueOf(longitudeEt.getText().toString());
            float accuracy = Float.valueOf(accuracyEt.getText().toString());
            mSpoofer.mockLocation(latitude, longitude, accuracy);
            message = "The location has been changed to (" + latitude + "," + longitude + ")";
        } else {
            message = "Spoofing the location has failed.";
        }

        CoordinatorLayout cl = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_layout);
        Snackbar.make(cl, message, Snackbar.LENGTH_SHORT).show();
    }

    public void setSpoofer(LocationSpoofer spoofer) {
        if (spoofer == null) {
            throw new IllegalArgumentException("Argument 'spoofer' cannot be null.");
        }
        mSpoofer = spoofer;
    }

    public void clearErrors(){
        EditText latitudeEt = (EditText) getView().findViewById(R.id.latitude);
        latitudeEt.setError(null);

        EditText longitudeEt = (EditText) getView().findViewById(R.id.longitude);
        longitudeEt.setError(null);

        EditText accuracyEt = (EditText) getView().findViewById(R.id.accuracy);
        accuracyEt.setError(null);
    }
}
