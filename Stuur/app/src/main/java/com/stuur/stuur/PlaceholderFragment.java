package com.stuur.stuur;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stuur.stuur.R;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static com.stuur.stuur.MainActivity.background_image;
import static com.stuur.stuur.MainActivity.censor_type;
import static com.stuur.stuur.MainActivity.censor_weight;
import static com.stuur.stuur.MainActivity.check_new_messages;
import static com.stuur.stuur.MainActivity.cur_group_name;
import static com.stuur.stuur.MainActivity.onCreateDialog;
import static com.stuur.stuur.MainActivity.receiveMsgAnimation;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    public static LayoutInflater inflater;
    public static ViewGroup container;
    public static View view;
    public static Boolean init = true;

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

        // hide/show contents for seperate pages
        final EditText msg_box = (EditText) rootView.findViewById(R.id.msg_box);
        Button add_friend_btn = (Button) rootView.findViewById(R.id.add_friend_btn);
        TextView stuur_key_txt = (TextView) rootView.findViewById(R.id.stuur_key_txt);
        ListView weight_list = (ListView) rootView.findViewById(R.id.censor_weight_list);
        final ListView type_list = (ListView) rootView.findViewById(R.id.censor_type_list);
        rootView.setTag("view" + section_num);

        stuur_key_txt.setTextColor(MainActivity.title_colors[background_image]);
        textView.setTextColor(MainActivity.title_colors[background_image]);
        init = false;

        if(section_num == 0) {

            if (censor_weight.equals("none"))
                type_list.setVisibility(rootView.GONE);
            else
                type_list.setVisibility(rootView.VISIBLE);

            //set visibilities and hide keyboard
            msg_box.setVisibility(rootView.GONE);
            add_friend_btn.setVisibility(rootView.GONE);
            stuur_key_txt.setVisibility(rootView.VISIBLE);
            weight_list.setVisibility(rootView.VISIBLE);
            MainActivity.hideKeyboard(getActivity());



            stuur_key_txt.setText("Your stuur key: " + MainActivity.user_key);

            //weight and type lists with chechboxes
            final String[] weights = {"Full Censor", "Partial Censor", "No Censor"};
            final String[] types = {"Grawlix Censor", "Emoji Censor", "Star Censor"};
            Boolean[] checked_weight = new Boolean[3];
            Boolean[] checked_type = new Boolean[3];

            //find which weight and type are set
            switch (censor_weight) {
                case "full":
                    Boolean[] tempfull = {true, false, false};
                    checked_weight = tempfull;
                    break;
                case "partial":
                    Boolean[] temppart = {false, true, false};
                    checked_weight = temppart;
                    break;
                case "none":
                    Boolean[] tempnone = {false, false, true};
                    checked_weight = tempnone;
                    break;
                default:
                    break;
            }

            switch (censor_type) {
                case "grawlix":
                    Boolean[] tempgraw = {true, false, false};
                    checked_type = tempgraw;
                    break;
                case "emoji":
                    Boolean[] tempemoji = {false, true, false};
                    checked_type = tempemoji;
                    break;
                case "star":
                    Boolean[] tempstar = {false, false, true};
                    checked_type = tempstar;
                    break;
                default:
                    break;
            }

            CheckBoxList adapter_weights = new CheckBoxList(getActivity(), weights, checked_weight);
            weight_list.setAdapter(adapter_weights);
            weight_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            MainActivity.censor_weight = "full";
                            type_list.setVisibility(rootView.VISIBLE);
                            MainActivity.saveWeight(view, "full");
                            break;
                        case 1:
                            MainActivity.censor_weight = "partial";
                            type_list.setVisibility(rootView.VISIBLE);
                            MainActivity.saveWeight(view, "partial");
                            break;
                        case 2:
                            MainActivity.censor_weight = "none";
                            type_list.setVisibility(rootView.GONE);
                            MainActivity.saveWeight(view, "none");
                            break;
                        default:
                            break;
                    }

                    String flag = "weight";
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    MainActivity.onChangeCheckbox(savedInstanceState, rootView, checkBox, flag);
                }
            });

            CheckBoxList adapter_types = new CheckBoxList(getActivity(), types, checked_type);
            type_list.setAdapter(adapter_types);
            type_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            MainActivity.censor_type = "grawlix";
                            MainActivity.saveType(view, "grawlix");
                            break;
                        case 1:
                            MainActivity.censor_type = "emoji";
                            MainActivity.saveType(view, "emoji");
                            break;
                        case 2:
                            MainActivity.censor_type = "star";
                            MainActivity.saveType(view, "star");
                            break;
                        default:
                            break;
                    }

                    String flag = "type";
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    MainActivity.onChangeCheckbox(savedInstanceState, rootView, checkBox, flag);
                }
            });

        } else if(section_num == 1) {
            // friend list page
            msg_box.setVisibility(rootView.GONE);
            add_friend_btn.setVisibility(rootView.VISIBLE);
            stuur_key_txt.setVisibility(rootView.GONE);
            weight_list.setVisibility(rootView.GONE);
            type_list.setVisibility(rootView.GONE);
            MainActivity.hideKeyboard(getActivity());

            // friends list
            MainActivity.refreshFriendsList(rootView, getActivity());

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
            weight_list.setVisibility(rootView.GONE);
            type_list.setVisibility(rootView.GONE);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState1) {
        super.onActivityCreated(savedInstanceState1);
        //if(MainActivity.init_checked_location & !MainActivity.permissions_denied) {
            getLocation(getActivity());
        //}
    }

    public void getLocation(Activity activity) {
        if (!MainActivity.init_checked_location || (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        } else if(!MainActivity.permissions_denied){
            if(MainActivity.location == null) return;
            double lng = MainActivity.location.getLongitude();
            double lat = MainActivity.location.getLatitude();

            String[] params_3 = {MainActivity.user_id, Double.toString(lat), Double.toString(lng)};
            NetworkTask network_task_3 = new NetworkTask("update_location", params_3);
            String[] resp_status_3 = new String[0];
            try {
                resp_status_3 = network_task_3.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            MainActivity.init_checked_location = true;
        }
    }
}
