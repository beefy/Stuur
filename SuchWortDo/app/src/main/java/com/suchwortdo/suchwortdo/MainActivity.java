package com.suchwortdo.suchwortdo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            int section_num = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            textView.setText(page_title[section_num]);

            // hide/show contents for seperate pages
            final EditText msg_box = (EditText) rootView.findViewById(R.id.msg_box);
            Button add_friend_btn = (Button) rootView.findViewById(R.id.add_friend_btn);
            if(section_num == 0) {
                // settings page
                msg_box.setVisibility(rootView.GONE);
                add_friend_btn.setVisibility(rootView.GONE);
                hideKeyboard(getActivity());
            } else if(section_num == 1) {
                // friend list page
                msg_box.setVisibility(rootView.GONE);
                add_friend_btn.setVisibility(rootView.VISIBLE);
                hideKeyboard(getActivity());

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

                msg_box.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if( keyCode == KeyEvent.KEYCODE_ENTER ) {
                            if( event.getAction() == KeyEvent.ACTION_UP ) {
                                if(msg_box.getText().length() == 0) {
                                    shakeAnimation(rootView);
                                } else {
                                    sendMsgAnimation(rootView);
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }

            return rootView;
        }
    }

    public static void shakeAnimation(View v) {
        Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
        v.findViewById(R.id.msg_box).startAnimation(shake);
    }

    public static void sendMsgAnimation(View v) {
        final View finalv = v;
        Animation move_up = AnimationUtils.loadAnimation(v.getContext(), R.anim.move_up);
        Animation replace = AnimationUtils.loadAnimation(v.getContext(), R.anim.replace);

        replace.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                EditText editText = (EditText) finalv.findViewById(R.id.msg_box);
                editText.setText("", TextView.BufferType.EDITABLE);

                CharSequence text = "Message Sent";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(finalv.getContext(), text, duration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, -200);
                toast.show();
            }
        });

        v.findViewById(R.id.anim_msg_box).setVisibility(View.VISIBLE);
        v.findViewById(R.id.msg_box).startAnimation(move_up);
        v.findViewById(R.id.anim_msg_box).startAnimation(replace);
        v.findViewById(R.id.anim_msg_box).setVisibility(View.INVISIBLE);
    }

    public static boolean send_msg() {
        return true;
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
