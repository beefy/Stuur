package com.suchwortdo.suchwortdo;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            String[] page_title = {"Settings","Friend List","Friends","Local","Global"};
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            int section_num = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            textView.setText(page_title[section_num]);

            // hide message box for settings and friend list screens
            EditText msg_box = (EditText) rootView.findViewById(R.id.msg_box);
            Button add_friend_btn = (Button) rootView.findViewById(R.id.add_friend_btn);
            if(section_num == 0) {
                // settings page
                msg_box.setVisibility(rootView.GONE);
                add_friend_btn.setVisibility(rootView.GONE);
            } else if(section_num == 1) {
                // friend list page
                msg_box.setVisibility(rootView.GONE);
                add_friend_btn.setVisibility(rootView.VISIBLE);

                // friends
                String[] friends = {"bob","george","sally","bob","george","sally","bob","george","sally","bob","george","sally"};
                ArrayAdapter<String> friends_Adapter =
                        new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friends);
                ListView friends_listView = (ListView) rootView.findViewById(R.id.friend_list);
                friends_listView.setAdapter(friends_Adapter);

                // keys
                String[] keys = {"5FWAS","234SD","WWWWW","5FWAS","234SD","WWWWW","5FWAS","234SD","WWWWW","5FWAS","234SD","WWWWW"};
                ArrayAdapter<String> keys_Adapter =
                        new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, keys);
                ListView keys_listView = (ListView) rootView.findViewById(R.id.key_list);
                keys_listView.setAdapter(keys_Adapter);
            } else {
                    msg_box.setVisibility(rootView.VISIBLE);
                add_friend_btn.setVisibility(rootView.GONE);
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Settings";
                case 1:
                    return "Friend List";
                case 2:
                    return "Friends";
                case 3:
                    return "Local";
                case 4:
                    return "Global";
            }
            return null;
        }
    }
}
