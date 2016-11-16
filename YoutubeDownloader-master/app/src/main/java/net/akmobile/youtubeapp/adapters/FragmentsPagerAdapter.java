package net.akmobile.youtubeapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import net.akmobile.youtubeapp.fragments.DownloadsFragment;
import net.akmobile.youtubeapp.fragments.SearchFragment;

/**
 * Created by Rahimli Rahim on 28/09/2016.
 * ragim95@gmail.com
 * https://github.com/rahimlis/
 */

/**
 * This class manages the horizontal scrolling swipe tabs,
 * see explanation of each  method on http:/developers.google.com
 */
public class FragmentsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    private String[] tabs;
    public FragmentsPagerAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return position == 0 ? SearchFragment.newInstance() : DownloadsFragment.newInstance();
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
