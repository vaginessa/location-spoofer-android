package com.emmanuelcorrales.locationspoofer.activities;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.emmanuelcorrales.android.utils.KeyboardUtils;
import com.emmanuelcorrales.android.utils.LocationUtils;
import com.emmanuelcorrales.android.utils.ViewPagerUtils;
import com.emmanuelcorrales.locationspoofer.R;
import com.emmanuelcorrales.locationspoofer.adapters.ViewPagerAdapter;
import com.emmanuelcorrales.locationspoofer.fragments.FormFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.LocationConfigDialogFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.MapHintDialogFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.MockConfigDialogFragment;
import com.emmanuelcorrales.locationspoofer.utils.ConfigUtils;
import com.emmanuelcorrales.locationspoofer.LocationSpoofer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AnalyticsActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_LOCATION = 7676;
    private static final int INDEX_MAP = 0;
    private static final int INDEX_FORM = 1;

    private GoogleMap mMap;
    private DialogFragment mMockConfigDialog = new MockConfigDialogFragment();
    private DialogFragment mLocationConfigDialog = new LocationConfigDialogFragment();
    private DialogFragment mMapHintDialog = new MapHintDialogFragment();
    private LocationSpoofer mSpoofer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpoofer = new LocationSpoofer(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SupportMapFragment mapFragment = getMapFragment();
        FormFragment formFragment = getFormFragment();
        Fragment[] fragments = {mapFragment, formFragment};
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(this);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!mSpoofer.canMockLocation()) {
            mMockConfigDialog.show(getSupportFragmentManager(), MockConfigDialogFragment.TAG);
        } else if (!LocationUtils.isGpnOn(this)) {
            mLocationConfigDialog.show(getSupportFragmentManager(), LocationConfigDialogFragment.TAG);
        } else {
            mSpoofer.initializeGpsSpoofing();
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            if (viewPager.getCurrentItem() == INDEX_MAP && ConfigUtils.isMapHintVisible(this)) {
                mMapHintDialog.show(getSupportFragmentManager(), MapHintDialogFragment.TAG);
            }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_mock_location)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSpoofer.mockLocation(latLng.latitude, latLng.longitude);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == INDEX_MAP) {
            View view = findViewById(android.R.id.content);
            KeyboardUtils.hideKeyboard(this, view);

            getFormFragment().clearErrors();

            if (ConfigUtils.isMapHintVisible(this)) {
                mMapHintDialog.show(getSupportFragmentManager(), MapHintDialogFragment.TAG);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private SupportMapFragment getMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                ViewPagerUtils.getViewPagerFragment(R.id.viewpager, getSupportFragmentManager(), 0);

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
        }
        return mapFragment;
    }

    private FormFragment getFormFragment() {
        FormFragment formFragment = (FormFragment)
                ViewPagerUtils.getViewPagerFragment(R.id.viewpager, getSupportFragmentManager(), 1);

        if (formFragment == null) {
            formFragment = FormFragment.newInstance(mSpoofer);
        } else {
            formFragment.setSpoofer(mSpoofer);
        }
        return formFragment;
    }
}
