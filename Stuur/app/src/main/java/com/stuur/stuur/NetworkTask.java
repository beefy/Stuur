package com.stuur.stuur;

import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by Nathaniel on 3/3/2016.
 */
// execute network tasks
/*
NetworkTask network_task = new NetworkTask();
        try {
        network_task.execute().get();
        } catch (InterruptedException e) {
        e.printStackTrace();
        } catch (ExecutionException e) {
        e.printStackTrace();
        }
*/
public class NetworkTask  extends AsyncTask<Void, String, String[]> {

    public static String endpoint;
    public static String[] parameters;
    public static String base_url = "http://ec2-52-37-150-66.us-west-2.compute.amazonaws.com";
    public static String port_num = ":8080";
    public static String send_msg_endpoint = "/send_msg?";
    public static String receive_msg_endpoint = "/receive_msg?";

    public NetworkTask(String endpoint, String[] parameters) {
        this.endpoint = endpoint;
        this.parameters = parameters;
    }

    public static String get_website_src(String url_str) throws IOException {
        URL url_obj;
        url_obj = new URL(url_str);
        URLConnection yc = url_obj.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        String full_src = "";
        while ((inputLine = in.readLine()) != null)
            full_src += inputLine;
        //full_src += inputLine + "\n"; //for proper formatting
        in.close();

        return full_src;
    }

    /**
     *
     * @param msg_text
     * @param sending_id
     * @return true if success, false otherwise
     */
    public static boolean send_msg(String msg_text, String sending_id, String group_name) {
        // encode message
        String encoded_msg = "";
        try {
            encoded_msg = StringEscapeUtils.escapeJava(URLEncoder.encode(msg_text, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        // send message
        String parameters = "msg_text=" + encoded_msg + "&sending_id=" + sending_id + "&group_name=" + group_name;
        String network_call = base_url+port_num+send_msg_endpoint+parameters;
        String resp = "";
        Boolean success = false;
        try {
            resp = get_website_src(network_call);

            // read response
            JSONObject obj = new JSONObject(resp);
            success = (obj.get("content").toString().equals("SUCCESS"));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return success;
    }

    public static String[] receive_msg(String receive_id, String group_name) {

        // send message
        String parameters = "receive_id=" + receive_id + "&group_name=" + group_name;
        String network_call = base_url+port_num+receive_msg_endpoint+parameters;
        String resp = "";
        String[] messages;
        try {
            resp = get_website_src(network_call);

            // read response
            JSONArray obj = (JSONArray) new JSONObject(resp).get("content");
            messages = new String[obj.length()];
            for(int i = 0; i < obj.length(); i++) {
                messages[i] = StringEscapeUtils.unescapeJava(URLDecoder.decode(obj.get(i).toString(), "UTF-8"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return messages;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        switch(endpoint) {
            case "send_msg":
                String[] out1 = {toString(send_msg(parameters[0], parameters[1], parameters[2]))};
                return out1;
            case "receive_msg":
                String[] out2 = receive_msg(parameters[0], parameters[1]);
                return out2;
            default:
                break;
        }
        return null;
    }

    public static String toString(boolean value) {
        return value ? "true" : "false";
    }
}
