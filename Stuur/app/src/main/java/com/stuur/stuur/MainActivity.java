package com.stuur.stuur;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.mutable.Mutable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {

    public static boolean init = true;
    public static ArrayList<String> remaining_messages_friends = new ArrayList<String>();
    public static ArrayList<String> remaining_messages_global = new ArrayList<String>();
    public static ArrayList<String> remaining_messages_local = new ArrayList<String>();
    public static boolean ongoing_animation = false;
    public static String cur_group_name = "friends";
    public static String android_id;
    public static String user_key;
    public static String user_id;
    public static LocationManager locationManager;
    public static boolean init_checked_location = false;
    public static String[] friend_nicks = {"You need some friends, loser"};
    public static String[] friend_keys = {""};
    public static boolean msg_sent_resp = false;
    public static String friend_added = "0";
    public static int[] background_images = {R.drawable.aurora, R.drawable.neuschwanstein, R.drawable.street,R.drawable.hillside, R.drawable.autumn};
    public static int[] title_colors = {Color.rgb(209,209,209),Color.rgb(99,99,99),Color.rgb(66,66,66), Color.rgb(99,99,99), Color.rgb(66,66,66)};
    public static int background_image = 0;
    public static boolean permissions_denied = false;
    public static Location location;

    public static String[] emoji_basic = {
            "\uDE00", "\uDE01", "\uDE02", "\uDE03", "\uDE04", "\uDE05", "\uDE06", "\uDE07", "\uDE08", "\uDE09", "\uDE0A", "\uDE0B", "\uDE0C", "\uDE0D", "\uDE0D", "\uDE0E", "\uDE0F",
            "\uDE10", "\uDE11", "\uDE12", "\uDE13", "\uDE14", "\uDE15", "\uDE16", "\uDE17", "\uDE18", "\uDE19", "\uDE1A", "\uDE1B", "\uDE1C", "\uDE1D", "\uDE1D", "\uDE1E", "\uDE1F",
            "\uDE20", "\uDE21", "\uDE22", "\uDE23", "\uDE24", "\uDE25", "\uDE26", "\uDE27", "\uDE28", "\uDE29", "\uDE2A", "\uDE2B", "\uDE2C", "\uDE2D", "\uDE2D", "\uDE2E", "\uDE2F",
            "\uDE30", "\uDE31", "\uDE32", "\uDE33", "\uDE34", "\uDE35", "\uDE36", "\uDE37", "\uDE38", "\uDE39", "\uDE3A", "\uDE3B", "\uDE3C", "\uDE3D", "\uDE3D", "\uDE3E", "\uDE3F",
    };

    //local Database setting info
    public static final String DEFAULT = "N/A";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SETTING_WEIGHT = "censorWeight";
    public static final String SETTING_TYPE = "censorType";

    public static final String MyFRIENDS = "MyFriends";
    public static final String FRIENDS = "local_friends";
    public static final String IDS = "local_ids";

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
    public static CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(2);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                // When swiping between pages, select the
                // corresponding tab.
                if (position == 2) {
                    cur_group_name = "friends";
                } else if (position == 3) {
                    cur_group_name = "local";
                } else {
                    cur_group_name = "global";
                }

                mViewPager.postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                        receiveMsgAnimation(activeView);
                    }
                }, 100);
            }
        });

        //unique(?) phone id, temporarily in the first friends list position
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // get user_key
        String[] params = {android_id};
        NetworkTask network_task = new NetworkTask("create_user", params);
        String[] resp_status = new String[0];
        try {
            resp_status = network_task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // get friends
        String[] params_2 = {MainActivity.user_id};
        NetworkTask network_task_2 = new NetworkTask("get_friends", params_2);
        String[] resp_status_2 = new String[0];
        try {
            resp_status_2 = network_task_2.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // get messages
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                check_new_messages(activeView);
            }
        }, 0, 5000);

        //get location
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        // set background image
        Display display= getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final View mainView = getWindow().getDecorView();
        Random random = new Random();
        background_image = random.nextInt(4);
        //background_image = 4;
        ImageView imageView = new ImageView(mainView.getContext());
        imageView.setImageResource(MainActivity.background_images[background_image]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bitmap unscaled_bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap scaled_bitmap = scaleCenterCrop(unscaled_bitmap,(int)(size.y),(int)(size.x));
        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        //scaled_bitmap.compress(Bitmap.CompressFormat.JPEG,100, out);
        //Bitmap compressed_bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

        BitmapDrawable bd = new BitmapDrawable(getResources(), scaled_bitmap);
        mainView.setBackground(bd);

        MainActivity.loadType(mViewPager);
        MainActivity.loadWeight(mViewPager);
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public static void onChangeCheckbox(Bundle savedInstanceState, View v, CheckBox checkBox, String flag){

        boolean isChecked = checkBox.isChecked();

        if (flag.equals("weight") && !isChecked){

            ListView list = (ListView) v.findViewById(R.id.censor_weight_list);
            removeAllChecks(list);
            checkBox.setChecked(true);

        } else if (flag.equals("type") && !isChecked){

            ListView list = (ListView) v.findViewById(R.id.censor_type_list);
            removeAllChecks(list);
            checkBox.setChecked(true);
        }

    }

    public static void removeAllChecks(ViewGroup vg) {
        View v = null;
        for(int i = 0; i < vg.getChildCount(); i++){
            try {
                v = vg.getChildAt(i);
                ((CheckBox)v).setChecked(false);
            }
            catch(Exception e1){ //if not checkBox, null View, etc
                try {
                    removeAllChecks((ViewGroup)v);
                }
                catch(Exception e2){ //v is not a view group
                    continue;
                }
            }
        }
    }

    // update friend dialog
    public static void onCreateDialog(View v, String nick, String key, int position) {

        final String selected_key = key;
        final String selected_nick = nick;
        final int final_position = position;
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_friend);

        final EditText keyText = (EditText) dialog.findViewById(R.id.key);
        final EditText nickText = (EditText) dialog.findViewById(R.id.nickname);
        keyText.setText(key);
        nickText.setText(nick);

        dialog.show();

        Button positive = (Button) dialog.findViewById(R.id.UpdateButton);
        Button negative = (Button) dialog.findViewById(R.id.CancelButton);
        Button delete = (Button) dialog.findViewById(R.id.DeleteButton);
        positive.setText("Update");
        delete.setVisibility(View.VISIBLE);

        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_title.setText("Update " + nick);

        positive.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (keyText.getText().toString().isEmpty()) {
                    Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                    keyText.startAnimation(shake);
                    Toast.makeText(v.getContext(), "Please enter a user key", Toast.LENGTH_SHORT).show();
                } else {

                    if (nickText.getText().toString() == null || nickText.getText().toString().length() == 0) {
                        nickText.setText("Stuur buddy");
                    }

                    if (!keyText.getText().toString().equals(selected_key)) {
                        // add friend
                        String[] params = {MainActivity.user_id, keyText.getText().toString()};
                        NetworkTask network_task = new NetworkTask("add_friend", params);
                        String[] resp_status = new String[0];
                        try {
                            resp_status = network_task.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        if (friend_added.equals("1")) {
                            deleteFriendLocally(v, final_position);
                            addFriendLocally(v, nickText.getText().toString(), final_position);
                            setFriendsLocally(v);

                            if (!selected_key.equals(keyText.getText().toString())) {
                                // delete old friend
                                String[] params_3 = {MainActivity.user_id, selected_key};
                                NetworkTask network_task_3 = new NetworkTask("delete_friend", params_3);
                                String[] resp_status_3 = new String[0];
                                try {
                                    resp_status_3 = network_task_3.execute().get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }

                            // refresh friends list
                            String[] params_2 = {MainActivity.user_id};
                            NetworkTask network_task_2 = new NetworkTask("get_friends", params_2);
                            String[] resp_status_2 = new String[0];
                            try {
                                resp_status_2 = network_task_2.execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                            Activity host = (Activity) activeView.getContext();
                            refreshFriendsList(activeView, host);

                            // display toast
                            Toast toast = Toast.makeText(v.getContext(), "Congrats! " + nickText.getText().toString() + " will make a great friend.", Toast.LENGTH_SHORT);
                            toast.show();

                            dialog.dismiss();
                        } else {
                            // shake key
                            Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                            keyText.startAnimation(shake);

                            // empty key
                            keyText.setText("");

                            // display toast
                            Toast toast = Toast.makeText(v.getContext(), "Friend not found :/", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        friend_added = "0";
                    } else {
                        deleteFriendLocally(v, final_position);
                        addFriendLocally(v, nickText.getText().toString(), final_position);
                        setFriendsLocally(v);
                        // display toast
                        Toast toast = Toast.makeText(v.getContext(), "Congrats! " + nickText.getText().toString() + " will make a great friend.", Toast.LENGTH_SHORT);
                        toast.show();

                        // refresh friends list
                        String[] params_2 = {MainActivity.user_id};
                        NetworkTask network_task_2 = new NetworkTask("get_friends", params_2);
                        String[] resp_status_2 = new String[0];
                        try {
                            resp_status_2 = network_task_2.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                        Activity host = (Activity) activeView.getContext();
                        refreshFriendsList(activeView, host);

                        dialog.dismiss();
                    }
                }
            }
        });

        delete.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                deleteFriendLocally(v,final_position);

                // delete friend
                String[] params = {MainActivity.user_id, selected_key};
                NetworkTask network_task = new NetworkTask("delete_friend", params);
                String[] resp_status = new String[0];
                try {
                    resp_status = network_task.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                // refresh friends list
                String[] params_2 = {MainActivity.user_id};
                NetworkTask network_task_2 = new NetworkTask("get_friends", params_2);
                String[] resp_status_2 = new String[0];
                try {
                    resp_status_2 = network_task_2.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                Activity host = (Activity) activeView.getContext();
                refreshFriendsList(activeView, host);

                // display toast
                Toast toast = Toast.makeText(v.getContext(), nickText.getText().toString() + " has been deleted", Toast.LENGTH_SHORT);
                toast.show();

                dialog.dismiss();
            }
        });

        negative.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // add friend dialog
    public static void onCreateDialog(Bundle savedInstanceState, View v) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_friend);

        final EditText keyText = (EditText) dialog.findViewById(R.id.key);
        final EditText nickText = (EditText) dialog.findViewById(R.id.nickname);

        dialog.show();

        Button positive = (Button) dialog.findViewById(R.id.UpdateButton);
        Button negative = (Button) dialog.findViewById(R.id.CancelButton);
        Button delete = (Button) dialog.findViewById(R.id.DeleteButton);
        positive.setText("Add");
        delete.setText("");
        delete.setClickable(false);

        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_title.setText("Add a Friend");

        positive.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (keyText.getText().toString().isEmpty()) {
                    Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                    keyText.startAnimation(shake);
                    Toast.makeText(v.getContext(), "Please enter a user key", Toast.LENGTH_SHORT).show();
                } else {

                    if (nickText.getText().toString() == null || nickText.getText().toString().length() == 0) {
                        nickText.setText("Stuur buddy");
                    }

                    // add friend
                    String[] params = {MainActivity.user_id, keyText.getText().toString()};
                    NetworkTask network_task = new NetworkTask("add_friend", params);
                    String[] resp_status = new String[0];
                    try {
                        resp_status = network_task.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (friend_added.equals("1")) {
                        addFriendLocally(v, nickText.getText().toString(), friend_nicks.length);
                        setFriendsLocally(v);

                        // refresh friends list
                        String[] params_2 = {MainActivity.user_id};
                        NetworkTask network_task_2 = new NetworkTask("get_friends", params_2);
                        String[] resp_status_2 = new String[0];
                        try {
                            resp_status_2 = network_task_2.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                        Activity host = (Activity) activeView.getContext();
                        refreshFriendsList(activeView, host);

                        // display toast
                        Toast toast = Toast.makeText(v.getContext(), "Congrats! " + nickText.getText().toString() + " will make a great friend.", Toast.LENGTH_SHORT);
                        toast.show();

                        dialog.dismiss();
                    } else {
                        // shake key
                        Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                        keyText.startAnimation(shake);

                        // empty key
                        keyText.setText("");

                        // display toast
                        Toast toast = Toast.makeText(v.getContext(), "Friend not found :/", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    friend_added = "0";
                }
            }
        });

        negative.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void addFriendLocally(View view, String name, int position){
        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyFRIENDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String added = "";
        for(int i = 0; i < friend_nicks.length+1; i++) {
            if(i == position) added += name + ",";
            if(i < friend_nicks.length) added += friend_nicks[i] + ",";
        }
        editor.putString(FRIENDS, added);
        editor.apply();
        //Toast.makeText(view.getContext(), "adding " + name + " locally", Toast.LENGTH_SHORT).show();
    }

    public static void deleteFriendLocally(View view, int position) {
        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyFRIENDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String[] cur_friends = getFriendsLocally(view).split(",");
        ArrayList<String> temp = new ArrayList<String>();
        for(int i = 0; i < cur_friends.length; i++) {
            if(i != position) {
                temp.add(cur_friends[i]);
            }
        }
        cur_friends = temp.toArray(new String[temp.size()]);
        friend_nicks = cur_friends;
        String added = "";
        for(int i = 0; i < cur_friends.length; i++) {
            added += cur_friends[i] + ",";
        }
        editor.putString(FRIENDS, added + ",");
        editor.apply();

    }

    public static String getFriendsLocally(View view){

        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyFRIENDS, Context.MODE_PRIVATE);
        String friends = sharedpreferences.getString(FRIENDS, DEFAULT);

        if (friends.equals(DEFAULT)) {
            //Toast.makeText(view.getContext(), "No friends currently", Toast.LENGTH_SHORT).show();
            return "";
        } else {
            //Toast.makeText(view.getContext(), friends, Toast.LENGTH_SHORT).show();
            return friends;
        }

    }

    public static void setFriendsLocally(View v) {
        String cur_friends = getFriendsLocally(v);
        String[] cur_friends_arr = cur_friends.split(",");
        friend_nicks = cur_friends_arr;
    }

    public static void refreshFriendsList(View v, Activity a) {
        if (friend_keys.length > 0) {
            final ListView friends_listView = (ListView) v.findViewById(R.id.friend_list);
            friends_listView.setVisibility(View.VISIBLE);
            friend_nicks = getFriendsLocally(v).split(",");
            CustomList adapter = new CustomList(a, friend_nicks, friend_keys);
            friends_listView.setAdapter(adapter);
            friends_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected_nick = friend_nicks[position];
                    String selected_key = friend_keys[position];
                    MainActivity.onCreateDialog(view, selected_nick, selected_key, position);
                }
            });
        } else {
            final ListView friends_listView = (ListView) v.findViewById(R.id.friend_list);
            friends_listView.setVisibility(View.GONE);
        }
    }

    public static void shakeMsgBox(View v) {
        Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
        v.findViewById(R.id.msg_box).startAnimation(shake);
    }

    public static void shakeAddFriendDialog(View v) {

        final View finalv = v;
        Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                CharSequence text = "User Not Found";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(finalv.getContext(), text, duration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, -200);
                toast.show();
            }
        });
        v.findViewById(R.id.add_friend_dialog).startAnimation(shake);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null) {
                            double lng = location.getLongitude();
                            double lat = location.getLatitude();

                            String[] params_3 = {user_id, Double.toString(lat), Double.toString(lng)};
                            NetworkTask network_task_3 = new NetworkTask("update_location", params_3);
                            String[] resp_status_3 = new String[0];
                            try {
                                resp_status_3 = network_task_3.execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            init_checked_location = true;
                        }
                    } catch(SecurityException e) {
                        //if(PlaceholderFragment.init) {
                            final Toast toast = Toast.makeText(getApplicationContext(), "Turn location on to send Local Messages!", Toast.LENGTH_LONG);
                            toast.show();
                        //}
                    }
                } else {
                    permissions_denied = true;
                    init_checked_location = true;
                }
                break;
            default:
                permissions_denied = true;
                init_checked_location = true;
                break;

        }
    }

    public static void check_new_messages(View v) {
        // to impact UI thread as little as possible
        //new Thread() {
          //  public void run() {

                // toast
        /*
                CharSequence text = "getting new messages";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(v.getContext(), text, duration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, -200);
                toast.show();
                */

                // GET FRIEND MESSAGES
                String[] params_friends = {user_id,"friends"};
                NetworkTask network_task_friends = new NetworkTask("receive_msg", params_friends);
                try {
                    String[] resp_status = network_task_friends.execute().get();
                    if(resp_status[0] != "success") return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                // GET LOCAL MESSAGES
                //if(!(ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    String[] params_local = {user_id, "local"};
                    NetworkTask network_task_local = new NetworkTask("receive_msg", params_local);
                    try {
                        String[] resp_status = network_task_local.execute().get();
                        if (resp_status[0] != "success") return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                //}

                // GET GLOBAL MESSAGES
                String[] params_global = {user_id,"global"};
                NetworkTask network_task_global = new NetworkTask("receive_msg", params_global);
                try {
                    String[] resp_status = network_task_global.execute().get();
                    if(resp_status[0] != "success") return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            //}
        //}.start();
    }

    public static void receiveMsgAnimation(View v) {
        final View finalv = v;

        Animation move_down = AnimationUtils.loadAnimation(v.getContext(), R.anim.move_down);
        Animation arrive = AnimationUtils.loadAnimation(v.getContext(), R.anim.arrive);

        ArrayList<String> cur_remaining_messages = new ArrayList<String>();
        if(cur_group_name == "friends") cur_remaining_messages = remaining_messages_friends;
        else if(cur_group_name == "local") cur_remaining_messages = remaining_messages_local;
        else cur_remaining_messages = remaining_messages_global;
        final ArrayList<String> final_cur_remaining_messages = cur_remaining_messages;

        final EditText reg_editText = (EditText) finalv.findViewById(R.id.msg_box);
        if (cur_remaining_messages.size() > 0 & !ongoing_animation) {
            arrive.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {

                    reg_editText.setFocusable(false);
                    mViewPager.setPagingEnabled(false);

                    hideKeyboard((Activity) finalv.getContext());
                    // I don't know why we need this if statement, but
                    // without it, the app crashes on build
                    if (final_cur_remaining_messages.size() > 0) {
                        EditText anim_editText = (EditText) finalv.findViewById(R.id.anim_msg_box);
                        String message = final_cur_remaining_messages.get(0);
                        anim_editText.setText(message);
                        final_cur_remaining_messages.remove(0);
                        ongoing_animation = true;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    EditText anim_editText = (EditText) finalv.findViewById(R.id.anim_msg_box);
                    EditText reg_editText = (EditText) finalv.findViewById(R.id.msg_box);
                    reg_editText.setText(anim_editText.getText());
                    ongoing_animation = false;
                }
            });
            v.findViewById(R.id.anim_msg_box).setVisibility(View.VISIBLE);
            v.findViewById(R.id.msg_box).startAnimation(move_down);
            v.findViewById(R.id.anim_msg_box).startAnimation(arrive);
            v.findViewById(R.id.anim_msg_box).setVisibility(View.INVISIBLE);
        } else if(reg_editText.getText().length() > 0 & !ongoing_animation){
            arrive.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    EditText anim_editText = (EditText) finalv.findViewById(R.id.anim_msg_box);
                    anim_editText.setText("");
                    ongoing_animation = true;
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {

                    reg_editText.setText("");
                    reg_editText.setFocusableInTouchMode(true);
                    mViewPager.setPagingEnabled(true);
                    ongoing_animation = false;
                }
            });

            v.findViewById(R.id.anim_msg_box).setVisibility(View.VISIBLE);
            v.findViewById(R.id.msg_box).startAnimation(move_down);
            v.findViewById(R.id.anim_msg_box).startAnimation(arrive);
            v.findViewById(R.id.anim_msg_box).setVisibility(View.INVISIBLE);
        }
    }

    public static void sendMsgAnimation(View v) {
        final View finalv = v;
        Animation move_up = AnimationUtils.loadAnimation(v.getContext(), R.anim.move_up);
        Animation replace = AnimationUtils.loadAnimation(v.getContext(), R.anim.replace);

        replace.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

                mViewPager.setPagingEnabled(false);
                ongoing_animation = true;
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                mViewPager.setPagingEnabled(true);

                EditText editText = (EditText) finalv.findViewById(R.id.msg_box);
                String msg_text = editText.getText().toString();

                // send message
                String[] params = {msg_text,user_id, cur_group_name};
                NetworkTask network_task = new NetworkTask("send_msg", params);
                String[] resp_status = new String[0];
                try {
                    resp_status = network_task.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                // toast
                CharSequence text;
                if(msg_sent_resp) text = "Message Sent";
                else text = "Network Error :(";
                msg_sent_resp = false;
                editText.setText("", TextView.BufferType.EDITABLE);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(finalv.getContext(), text, duration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, -200);
                toast.show();

                ongoing_animation = false;
            }
        });

        EditText anim_edit_text = (EditText) v.findViewById(R.id.anim_msg_box);
        anim_edit_text.setText("");

        if(!ongoing_animation) {
            v.findViewById(R.id.anim_msg_box).setVisibility(View.VISIBLE);
            v.findViewById(R.id.msg_box).startAnimation(move_up);
            v.findViewById(R.id.anim_msg_box).startAnimation(replace);
            v.findViewById(R.id.anim_msg_box).setVisibility(View.INVISIBLE);
        }
    }

    public static String censor_weight = "partial"; // none | full | partial
    public static String censor_type = "emoji"; // star | grawlix | emoji | cute | synonym
    public static String censor(String msg, String profanity_flags) {
        if(censor_weight == "none") return msg;
        // for every word
        for(int i = 0; i < profanity_flags.length(); i++) {
            if(profanity_flags.charAt(i) == '1') {
                // censor word
                String cur_word = getNthWord(msg,i);
                int start, end;
                if(censor_weight == "full") {
                    start = 0;
                    end = cur_word.length();
                } else {
                    start = 1;
                    end = cur_word.length()-1;
                }

                String censored_word = "";
                // for each letter in the word
                for(int y = start; y < end; y++) {
                    switch (censor_type) {
                        case "star":
                            censored_word += "*";
                            break;
                        case "grawlix":
                            ArrayList<String> grawlix_chars = new ArrayList<>(Arrays.asList("@","#","$","%","&","!"));
                            char last_char;
                            if(censored_word.length() > 0) last_char = censored_word.charAt(censored_word.length()-1);
                            else if(censor_weight == "partial") last_char = cur_word.charAt(0);
                            else last_char = '\n'; // something not in gralix_chars[]

                            if(grawlix_chars.contains(last_char + "")) {
                                grawlix_chars.remove(last_char + "");
                            }

                            int index = new Random().nextInt(grawlix_chars.size());
                            censored_word += grawlix_chars.get(index);
                            break;
                        case "emoji":
                            int emoji_num = new Random().nextInt(emoji_basic.length-1);
                            censored_word += "\uD83D" + emoji_basic[emoji_num];
                            /*
                            if((new Random().nextInt(emoji_d83c.length + emoji_d83d.length)) > emoji_d83c.length) {
                                int emoji_num = new Random().nextInt(emoji_d83d.length-1);
                                censored_word += "\uD83D" + emoji_d83d[emoji_num];
                            } else {
                                int emoji_num = new Random().nextInt(emoji_d83c.length-1);
                                censored_word += "\uD83C" + emoji_d83c[emoji_num];
                            }
                            */
                            break;
                        case "cute":
                            break;
                        case "synonym":
                            break;
                        default:
                            break;
                    }
                }

                if(censor_weight == "partial") {
                    censored_word = cur_word.charAt(0) + censored_word + cur_word.charAt(cur_word.length()-1);
                }

                // replace word in message
                msg = msg.replace(cur_word,censored_word);
            }
        }
        return msg;
    }

    public static String getNthWord(String full, int i) {
        String[] temp = full.split(" ");
        if(i-1 < temp.length) return temp[i];
        return null;
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

    public static void saveWeight(View view, String string){
        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SETTING_WEIGHT, string);
        editor.apply();
    }

    public static void loadWeight(View view){

        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String weight = sharedpreferences.getString(SETTING_WEIGHT, DEFAULT);

        //on startup set to full and grawlix
        if (weight.equals(DEFAULT)) censor_weight = "full";
        else censor_weight = weight;
    }

    public static void saveType(View view, String string){
        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SETTING_TYPE, string);
        editor.apply();
    }

    public static void loadType(View view){

        SharedPreferences sharedpreferences;
        sharedpreferences = view.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String type = sharedpreferences.getString(SETTING_TYPE, DEFAULT);

        if (type.equals(DEFAULT)) censor_type = "grawlix";
        else censor_type = type;
    }
}
