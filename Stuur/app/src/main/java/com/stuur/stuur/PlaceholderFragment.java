package com.stuur.stuur;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stuur.stuur.R;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.stuur.stuur.MainActivity.check_new_messages;
import static com.stuur.stuur.MainActivity.cur_group_name;
import static com.stuur.stuur.MainActivity.onCreateDialog;
import static com.stuur.stuur.MainActivity.receiveMsgAnimation;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    public static boolean init = true;
    public static LayoutInflater inflater;
    public static ViewGroup container;
    public static View view;

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
                             final Bundle savedInstanceState) {

        this.container = container;
        this.inflater = inflater;
        String[] page_title = {"Settings","Friend List","Friends","Local","Global"};
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.view = rootView;
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        int section_num = getArguments().getInt(ARG_SECTION_NUMBER)-1;
        textView.setText(page_title[section_num]);

        if(!MainActivity.init_checked_location) MainActivity.update_location(rootView.getContext());

        // hide/show contents for seperate pages
        final EditText msg_box = (EditText) rootView.findViewById(R.id.msg_box);
        Button add_friend_btn = (Button) rootView.findViewById(R.id.add_friend_btn);
        TextView stuur_key_txt = (TextView) rootView.findViewById(R.id.stuur_key_txt);
        ListView settings_list = (ListView) rootView.findViewById(R.id.settings_list);
        rootView.setTag("view" + section_num);
        if(section_num == 0) {
            // settings page
            msg_box.setVisibility(rootView.GONE);
            add_friend_btn.setVisibility(rootView.GONE);
            stuur_key_txt.setVisibility(rootView.VISIBLE);
            settings_list.setVisibility(rootView.VISIBLE);
            MainActivity.hideKeyboard(getActivity());

            stuur_key_txt.setText("Your stuur key: " + MainActivity.user_key);
            final String[] settings = {"Setting 1", "Setting 2", "Setting 3", "Setting 4"};
            CheckBoxList adapter = new CheckBoxList(getActivity(), settings);
            settings_list.setAdapter(adapter);

        } else if(section_num == 1) {
            // friend list page
            msg_box.setVisibility(rootView.GONE);
            add_friend_btn.setVisibility(rootView.VISIBLE);
            stuur_key_txt.setVisibility(rootView.GONE);
            settings_list.setVisibility(rootView.GONE);
            MainActivity.hideKeyboard(getActivity());

            final String[] friends = {"bob","george","sally","bob","george","sally","bob","george","sally","bob","george","sally"};
            final String[] keys = {"5FWAS","234SD","WWWWW","5FWAS","234SD","WWWWW","5FWAS","234SD","WWWWW","5FWAS","234SD","WWWWW"};

            // friends list
            final ListView friends_listView = (ListView) rootView.findViewById(R.id.friend_list);
            CustomList adapter = new CustomList(getActivity(), friends, keys);
            friends_listView.setAdapter(adapter);
            friends_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected_nick = friends[position];
                    String selected_key = keys[position];
                    MainActivity.onCreateDialog(savedInstanceState, view, selected_nick, selected_key);
                }
            });

            // add friend button
            add_friend_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.onCreateDialog(savedInstanceState, v);
                }
            });

        } else {
            msg_box.setVisibility(rootView.VISIBLE);
            add_friend_btn.setVisibility(rootView.GONE);
            stuur_key_txt.setVisibility(rootView.GONE);
            settings_list.setVisibility(rootView.GONE);

            msg_box.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            if (msg_box.getText().length() == 0) {
                                MainActivity.shakeMsgBox(rootView);
                            } else {
                                MainActivity.sendMsgAnimation(rootView);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });

            // click screen to see next message
            RelativeLayout rlayout = (RelativeLayout) rootView.findViewById(R.id.fragmentxml);
            rlayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!msg_box.isFocusable()) {
                        receiveMsgAnimation(rootView);
                    }
                }
            });

            // click message to see next message
            /*
            msg_box.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    receiveMsgAnimation(v, final_group_name);
                }
            });
            */
        }

        return rootView;
    }
}
