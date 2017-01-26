package com.emmanuelcorrales.locationspoofer.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

public final class ViewPagerUtils {

    private ViewPagerUtils() {

    }

    public static Fragment getViewPagerFragment(ViewPager viewPager, FragmentManager fm, int index) {
        return getViewPagerFragment(viewPager.getId(), fm, index);
    }

    public static Fragment getViewPagerFragment(int viewPagerId, FragmentManager fm, int index) {
        return fm.findFragmentByTag("android:switcher:" + viewPagerId + ":" + index);
    }
}
