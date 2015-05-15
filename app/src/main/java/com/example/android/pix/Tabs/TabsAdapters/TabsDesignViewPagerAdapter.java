package com.example.android.pix.Tabs.TabsAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.pix.Tabs.TabsViews.TabDesignGetStarted;
import com.example.android.pix.Tabs.TabsViews.TabDesignMaterialDesign;
import com.example.android.pix.Tabs.TabsViews.TabDesignPattern;
import com.example.android.pix.Tabs.TabsViews.TabDesignStyle;

public class TabsDesignViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    int tabNumber;

    // Constructor
    public TabsDesignViewPagerAdapter (FragmentManager fragmentManager, CharSequence titles[], int tabNumber) {
        super(fragmentManager);

        this.titles = titles;
        this.tabNumber = tabNumber;

    }

    // Return Fragment for each position
    @Override
    public Fragment getItem(int position) {
        TabDesignGetStarted tabDesignGetStarted = new TabDesignGetStarted();
        TabDesignMaterialDesign tabDesignMaterialDesign = new TabDesignMaterialDesign();
        TabDesignStyle tabDesignStyle = new TabDesignStyle();
        TabDesignPattern tabDesignPattern = new TabDesignPattern();
        switch (position) {
            case 0:
                return tabDesignGetStarted;
            case 1:
                return tabDesignMaterialDesign;
            case 2:
                return tabDesignStyle;
            case 3:
                return tabDesignPattern;
        }
        return null;
    }

    // Return tab titles for each position

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // Return tab number.
    @Override
    public int getCount() {
        return tabNumber;
    }
}