package com.emmanuelcorrales.locationspoofer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.emmanuelcorrales.locationspoofer.fragments.FormFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.MapHintDialogFragment;
import com.emmanuelcorrales.locationspoofer.fragments.dialogs.MockConfigDialogFragment;
import com.emmanuelcorrales.locationspoofer.utils.ConfigUtils;
import com.emmanuelcorrales.locationspoofer.utils.MockLocationUtils;
import com.emmanuelcorrales.locationspoofer.utils.ViewPagerUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_LOCATION = 7676;
    private static final int DEFAULT_ACCURACY = 5;
    private static final int INDEX_MAP = 0;
    private static final int INDEX_FORM = 1;

    private LocationManager mLocationManager;
    private DialogFragment mMockConfigDialog = new MockConfigDialogFragment();
    private DialogFragment mMapHintDialog = new MapHintDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLocationManager();

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
    protected void onResume() {
        super.onResume();
        if (!MockLocationUtils.isMockLocationEnabled(this)) {
            mMockConfigDialog.show(getSupportFragmentManager(), MockConfigDialogFragment.TAG);
            return;
        }

        if (ConfigUtils.isMapHintVisible(this)) {
            mMapHintDialog.show(getSupportFragmentManager(), MapHintDialogFragment.TAG);
        }
    }

    @Override
    protected void onPause() {
        if (mMockConfigDialog.isAdded()) {
            mMockConfigDialog.dismiss();
        }

        if (mMapHintDialog.isAdded()) {
            mMapHintDialog.dismiss();
        }

        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapLongClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCATION);
            }
        } else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_mock_location)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mockLocation(latLng.latitude, latLng.longitude);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            hideKeyboard();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setupLocationManager() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (MockLocationUtils.isMockLocationEnabled(this)) {
            mLocationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,           //name
                    false,                                  //requiresNetwork
                    false,                                  //requiresSatellite
                    false,                                  //requiresCell
                    false,                                  //hasMonetaryCost
                    true,                                   //supportsAltitude
                    true,                                   //supportsSpeed
                    true,                                   //supportsBearing
                    0,                                      //powerRequirement
                    5                                       //accuracy
            );
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        }
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
            formFragment = FormFragment.newInstance(mLocationManager);
        } else {
            formFragment.setLocationManager(mLocationManager);
        }
        return formFragment;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = findViewById(android.R.id.content);
        imm.hideSoftInputFromWindow(view.getWindowToken(), INDEX_MAP);
    }

    private void mockLocation(double latitude, double longitude) {
        mockLocation(latitude, longitude, DEFAULT_ACCURACY);
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
