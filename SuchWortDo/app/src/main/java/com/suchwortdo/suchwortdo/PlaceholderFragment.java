package com.suchwortdo.suchwortdo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import static com.suchwortdo.suchwortdo.MainActivity.hideKeyboard;
import static com.suchwortdo.suchwortdo.MainActivity.onCreateDialog;
import static com.suchwortdo.suchwortdo.MainActivity.sendMsgAnimation;
import static com.suchwortdo.suchwortdo.MainActivity.shakeAnimation;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
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

            // add friend button
            add_friend_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = onCreateDialog(savedInstanceState, v);
                    dialog.show();

                }
            });

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
