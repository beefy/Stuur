package com.stuur.stuur;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.ExecutionException;

public class StuurLocationListener implements LocationListener{
    @Override
    public void onLocationChanged(Location location) {
        Log.e("LOL", "Location changed");
        try {
            if (location != null) {
                double lng = location.getLongitude();
                double lat = location.getLatitude();

                String[] params_3 = {MainActivity.user_id, Double.toString(lat), Double.toString(lng)};
                NetworkTask network_task_3 = new NetworkTask("update_location", params_3);
                String[] resp_status_3 = new String[0];
                try {
                    resp_status_3 = network_task_3.execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
