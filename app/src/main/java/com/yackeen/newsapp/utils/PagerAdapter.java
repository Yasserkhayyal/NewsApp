package com.yackeen.newsapp.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yackeen.newsapp.GridViewFragment;
import com.yackeen.newsapp.ListViewFragment;

/**
 * Created by Mohamed Yasser on 3/5/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ListViewFragment tab1 = new ListViewFragment();
                return tab1;
            case 1:
                GridViewFragment tab2 = new GridViewFragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}