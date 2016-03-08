package com.stuur.stuur;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


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

    public static String[] emoji_basic = {
            "\uDE00","\uDE01","\uDE02","\uDE03","\uDE04","\uDE05","\uDE06","\uDE07","\uDE08","\uDE09","\uDE0A","\uDE0B","\uDE0C","\uDE0D","\uDE0D","\uDE0E","\uDE0F",
            "\uDE10","\uDE11","\uDE12","\uDE13","\uDE14","\uDE15","\uDE16","\uDE17","\uDE18","\uDE19","\uDE1A","\uDE1B","\uDE1C","\uDE1D","\uDE1D","\uDE1E","\uDE1F",
            "\uDE20","\uDE21","\uDE22","\uDE23","\uDE24","\uDE25","\uDE26","\uDE27","\uDE28","\uDE29","\uDE2A","\uDE2B","\uDE2C","\uDE2D","\uDE2D","\uDE2E","\uDE2F",
            "\uDE30","\uDE31","\uDE32","\uDE33","\uDE34","\uDE35","\uDE36","\uDE37","\uDE38","\uDE39","\uDE3A","\uDE3B","\uDE3C","\uDE3D","\uDE3D","\uDE3E","\uDE3F",
    };

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
                },100);
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
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                View activeView = (View) mViewPager.findViewWithTag("view" + mViewPager.getCurrentItem());
                check_new_messages(activeView);
            }
        }, 0, 5000);

        // set status/notification bar transparent
        // only works for newer android versions
        /*
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        */
    }

    public static Dialog onCreateDialog(Bundle savedInstanceState, View v, String nick, String key) {

        final View vfinal = v;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(v.getContext());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_add_friend, null);
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        shakeAddFriendDialog(vfinal);
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // delete the user
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        EditText nickname_widget = (EditText) dialogView.findViewById(R.id.nickname);
        EditText key_widget = (EditText) dialogView.findViewById(R.id.key);
        TextView title = (TextView) dialogView.findViewById(R.id.dialog_title);
        nickname_widget.setText(nick);
        key_widget.setText(key);
        title.setText("Edit " + nick);

        return builder.create();
    }

    public static Dialog onCreateDialog(Bundle savedInstanceState, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(v.getContext());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_add_friend, null))
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // add the user
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
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

                for(int i = 0; i < NetworkTask.resp[0].length; i++) {
                    remaining_messages_friends.add(censor(NetworkTask.resp[0][i], NetworkTask.resp[1][i]));
                }
                NetworkTask.resp = null;

                // GET LOCAL MESSAGES
                String[] params_local = {user_id,"local"};
                NetworkTask network_task_local = new NetworkTask("receive_msg", params_local);
                try {
                    String[] resp_status = network_task_local.execute().get();
                    if(resp_status[0] != "success") return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < NetworkTask.resp[0].length; i++) {
                    remaining_messages_local.add(censor(NetworkTask.resp[0][i], NetworkTask.resp[1][i]));
                }
                NetworkTask.resp = null;

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

                for(int i = 0; i < NetworkTask.resp[0].length; i++) {
                    remaining_messages_global.add(censor(NetworkTask.resp[0][i], NetworkTask.resp[1][i]));
                }
                NetworkTask.resp = null;
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
                if(resp_status != null && resp_status[0] == "success" && NetworkTask.resp[0][0] == "true") text = "Message Sent";
                else text = "Network Error :(";
                NetworkTask.resp = null;
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
}
