package com.example.android.pix.Adapter;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.pix.Fragment.InboxFragment;
import com.example.android.pix.Fragment.MembersFragment;
import com.example.android.pix.R;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class PagerCustomAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public PagerCustomAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return new InboxFragment();
            case 1:
                return new MembersFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }

    public int getIcon(int position){
        switch (position){
            case 0:
                return R.mipmap.ic_inbox_white_24dp;
            case 1:
                return R.mipmap.ic_people_outline_white_24dp;
        }
        return R.mipmap.ic_people_outline_grey600_24dp;
    }
}