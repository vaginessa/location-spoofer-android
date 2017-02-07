package com.emmanuelcorrales.locationspoofer.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.emmanuelcorrales.android.utils.LocationUtils;
import com.emmanuelcorrales.locationspoofer.LocationSpoofer;
import com.emmanuelcorrales.locationspoofer.R;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.LocationConfigDialogFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.MapHintDialogFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.MockConfigDialogFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.SpoofDialogFragment;
import com.emmanuelcorrales.locationspoofer.utils.ConfigUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AnalyticsActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_LOCATION = 7676;

    private GoogleMap mMap;
    private DialogFragment mMockConfigDialog = new MockConfigDialogFragment();
    private DialogFragment mLocationConfigDialog = new LocationConfigDialogFragment();
    private DialogFragment mMapHintDialog = new MapHintDialogFragment();
    private DialogFragment mSpoofDialog = new SpoofDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!LocationSpoofer.canMockLocation(this)) {
            mMockConfigDialog.show(getSupportFragmentManager(), MockConfigDialogFragment.TAG);
        } else if (!LocationUtils.isGpnOn(this)) {
            mLocationConfigDialog.show(getSupportFragmentManager(), LocationConfigDialogFragment.TAG);
        } else if (ConfigUtils.isMapHintVisible(this)) {
            mMapHintDialog.show(getSupportFragmentManager(), MapHintDialogFragment.TAG);
        }
    }

    @Override
    protected void onPause() {
        if (mMockConfigDialog.isAdded()) {
            mMockConfigDialog.dismiss();
        }

        if (mLocationConfigDialog.isAdded()) {
            mLocationConfigDialog.dismiss();
        }

        if (mMapHintDialog.isAdded()) {
            mMapHintDialog.dismiss();
        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && mMap != null) {

            Log.d(TAG, "Permission granted.");
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.d(TAG, "Permission granted but failed to enable my location.");
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Permission not granted.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCATION);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Bundle args = new Bundle();
        args.putDouble(SpoofDialogFragment.KEY_LATITUDE, latLng.latitude);
        args.putDouble(SpoofDialogFragment.KEY_LONGITUDE, latLng.longitude);
        mSpoofDialog.setArguments(args);
        mSpoofDialog.show(getSupportFragmentManager(), SpoofDialogFragment.TAG);
    }

    @Override
    public void onClick(View v) {
        mSpoofDialog.show(getSupportFragmentManager(), SpoofDialogFragment.TAG);
    }
}
