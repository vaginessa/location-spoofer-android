package com.emmanuelcorrales.locationspoofer.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AnalyticsActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, View.OnClickListener, GoogleMap.OnMarkerDragListener,
        SpoofDialogFragment.OnSpoofListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_LOCATION = 7676;
    private static final String KEY_SATE_MARKER_LATLNG = "key_state_marker_latlng";

    private GoogleMap mMap;
    private Marker mMarker;
    private DialogFragment mMockConfigDialog = new MockConfigDialogFragment();
    private DialogFragment mLocationConfigDialog = new LocationConfigDialogFragment();
    private DialogFragment mMapHintDialog = new MapHintDialogFragment();
    private LatLng mPreviousMarkerLatLng;

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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        LatLng latLng = (LatLng) savedInstanceState.get(KEY_SATE_MARKER_LATLNG);
        if (latLng == null) {
            return;
        }
        mPreviousMarkerLatLng = latLng;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!LocationSpoofer.canMockLocation(this)) {
            mMockConfigDialog.show(getSupportFragmentManager(), MockConfigDialogFragment.TAG);
        } else if (!LocationUtils.isGpnOn(this) && !mLocationConfigDialog.isAdded()) {
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
    protected void onSaveInstanceState(Bundle outState) {
        if (mMarker == null) {
            return;
        }
        outState.putParcelable(KEY_SATE_MARKER_LATLNG, mMarker.getPosition());
        super.onSaveInstanceState(outState);
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
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
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
        if (mPreviousMarkerLatLng != null) {
            moveDefaultMarker(mPreviousMarkerLatLng);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        DialogFragment dialogFragment = SpoofDialogFragment.newInstance(this, latLng);
        dialogFragment.show(getSupportFragmentManager(), SpoofDialogFragment.TAG);
    }

    @Override
    public void onClick(View v) {
        DialogFragment dialogFragment;
        if (mMarker == null) {
            dialogFragment = SpoofDialogFragment.newInstance(this);
        } else {
            dialogFragment = SpoofDialogFragment.newInstance(this, mMarker.getPosition());
        }
        dialogFragment.show(getSupportFragmentManager(), SpoofDialogFragment.TAG);
    }

    @Override
    public void onSpoof(LatLng latLng) {
        moveDefaultMarker(latLng);

        CoordinatorLayout coordinatorLayout =
                (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Snackbar.make(coordinatorLayout,
                "Spoofed location at " + latLng.latitude + "," + latLng.longitude + ".",
                Snackbar.LENGTH_SHORT).show();

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mPreviousMarkerLatLng = latLng;
    }

    @Override
    public void onSpoofCancel() {
        if (mPreviousMarkerLatLng != null) {
            mMarker.setPosition(mPreviousMarkerLatLng);
        }
    }

    private void moveDefaultMarker(LatLng latLng) {
        if (mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true));
        } else {
            mMarker.setPosition(latLng);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Don't use marker.getPosition()
        //The marker shifts up by some distance when I start dragging.
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        DialogFragment dialogFragment = SpoofDialogFragment.newInstance(this, marker.getPosition());
        dialogFragment.show(getSupportFragmentManager(), SpoofDialogFragment.TAG);
    }
}
