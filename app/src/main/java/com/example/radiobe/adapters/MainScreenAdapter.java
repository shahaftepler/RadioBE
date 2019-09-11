package com.example.radiobe.adapters;


import com.example.radiobe.fragments.AllPrograms;
import com.example.radiobe.fragments.Favorites;
import com.example.radiobe.fragments.Notifications;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MainScreenAdapter extends FragmentPagerAdapter {

        public MainScreenAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
//                case 0:
//                    return new ExoPlayerView();
//                    return new AllPrograms();
                case 0:
                    return new Notifications();
                case 1:
                    return new Favorites();
                case 2:
                    return new AllPrograms();
            }
            throw new IllegalArgumentException("No");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }