package com.emmanuelcorrales.locationspoofer;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mockLocationBtn = (Button) findViewById(R.id.mock_location);
        mockLocationBtn.setOnClickListener(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false,
                false,
                false,
                false,
                true,
                true,
                true,
                0,
                5
        );
        mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
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

    private LocationManager getLocationManager() {
        return (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        EditText latitudeEt = (EditText) findViewById(R.id.latitude);
        EditText longitudeEt = (EditText) findViewById(R.id.longitude);
        EditText accuracyEt = (EditText) findViewById(R.id.accuracy);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
}
