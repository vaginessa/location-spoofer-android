package com.emmanuelcorrales.locationspoofer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments;

    public ViewPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);

        if (fragments == null) {
            throw new IllegalArgumentException("Argument 'fragments' cannot be null.");
        }
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Map";

            case 1:
                return "Lat/Lng";
        }
        return super.getPageTitle(position);
    }
}
