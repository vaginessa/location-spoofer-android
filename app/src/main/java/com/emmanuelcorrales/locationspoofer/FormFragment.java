package com.emmanuelcorrales.locationspoofer;


import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FormFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = FormFragment.class.getSimpleName();

    public static FormFragment newInstance(LocationManager locationManager) {
        FormFragment fragment = new FormFragment();
        fragment.setLocationManager(locationManager);
        return fragment;
    }

    private LocationManager mLocationManager;

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
        EditText latitudeEt = (EditText) getView().findViewById(R.id.latitude);
        EditText longitudeEt = (EditText) getView().findViewById(R.id.longitude);
        EditText accuracyEt = (EditText) getView().findViewById(R.id.accuracy);
        String message;
        if (validateEditText(latitudeEt) | validateEditText(longitudeEt)) {
            double latitude = Double.valueOf(latitudeEt.getText().toString());
            double longitude = Double.valueOf(longitudeEt.getText().toString());
            float accuracy = Float.valueOf(accuracyEt.getText().toString());
            mockLocation(latitude, longitude, accuracy);
            message = "The location has been changed to (" + latitude + "," + longitude + ")";
        } else {
            message = "Spoofing the location has failed.";
        }
        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void setLocationManager(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    private boolean validateEditText(EditText editText) {
        if (editText == null) {
            throw new IllegalArgumentException("Argument 'editText' cannot be null.");
        }

        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.validation_required));
            return false;
        }
        return true;
    }

    private void mockLocation(double latitude, double longitude, float accuracy) {
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(latitude);
        mockLocation.setLongitude(longitude);
        mockLocation.setAccuracy(accuracy);
        mockLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
    }
}
