package com.example.csc8099dissertationproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

/**
 *  UserLocationActivity provides User location tracking service,
 *  once it starts, the service keeps running until user terminates the application.
 *
 *  @author harrietkim
 *  @version 01 (2019-08-20)
 */

public class UserLocationActivity extends AppCompatActivity {

    private UserLocationService userLocationService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            UserLocationService.UserLocationBinder userLocationBinder = (UserLocationService.UserLocationBinder) iBinder;
            userLocationService = userLocationBinder.getBinder();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound =false;
        }
    };

    private List<String> listProviders;
    private TextView tvGpsEnable;
    private TextView tvNetworkEnable;
    private TextView tvPassiveEnable;
    private TextView tvGpsLatitude;
    private TextView tvGpsLongitude;
    private TextView tvNetworkLatitude;
    private TextView tvNetworkLongitude;
    private TextView tvPassiveLatitude;
    private TextView tvPassiveLongitude;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, UserLocationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            unbindService(serviceConnection);
            bound =false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // TextView Internationalization: When calling TextView#setText
    // never call Number#toString() to format numbers */
    @SuppressLint("SetTextI18n")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_location_test);

        tvGpsEnable = findViewById(R.id.tvGpsEnable);
        tvNetworkEnable = findViewById(R.id.tvNetworkEnable);
        tvPassiveEnable = findViewById(R.id.tvPassiveEnable);

        tvGpsLatitude = findViewById(R.id.tvGpsLatitude);
        tvGpsLongitude = findViewById(R.id.tvGpsLongitude);
        tvNetworkLatitude = findViewById(R.id.tvNetworkLatitude);
        tvNetworkLongitude = findViewById(R.id.tvNetworkLongitude);
        tvPassiveLatitude = findViewById(R.id.tvPassiveLatitude);
        tvPassiveLongitude = findViewById(R.id.tvPassiveLongitude);


        int check = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        int check2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (check != PackageManager.PERMISSION_GRANTED || check2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Continuously query userLocationService
            updateProviderInformation();
            displayCurrentLocation();
        }
    }

    // Updates gps location by querying userLocationService
    private void displayCurrentLocation() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                Location gpsLocation = null;
                Location networkLocation = null;
                Location passiveLocation = null;

                if(userLocationService != null) {
                    gpsLocation = userLocationService.getLastLocation("gps");
                    networkLocation = userLocationService.getLastLocation("network");
                    passiveLocation = userLocationService.getLastLocation("passive");
                }

                if(gpsLocation != null) {
                    tvGpsLatitude.setText(": " + gpsLocation.getLatitude());
                    tvGpsLongitude.setText(": " + gpsLocation.getLongitude());
                }
                if(networkLocation != null) {
                    tvNetworkLatitude.setText(": " + networkLocation.getLatitude());
                    tvNetworkLongitude.setText(": " + networkLocation.getLongitude());
                }
                if(passiveLocation != null) {
                    tvPassiveLatitude.setText(": " + passiveLocation.getLatitude());
                    tvPassiveLongitude.setText(": " + passiveLocation.getLongitude());
                }

                handler.postDelayed(this, 500); // run this runnable again every 500ms so that it continuously updates the corresponding TextView
            }
        });
    }

    private boolean updated = false;
    private void updateProviderInformation() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // get available providers from userlocationservice just once
                if(userLocationService != null) {
                    listProviders = userLocationService.getLocationManager().getAllProviders();
                    boolean[] isEnable = new boolean[3];
                    for (String listProvider : listProviders) {
                        switch (listProvider) {
                            case LocationManager.GPS_PROVIDER:
                                isEnable[0] = userLocationService.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);
                                tvGpsEnable.setText(": " + isEnable[0]);
                                break;
                            case LocationManager.NETWORK_PROVIDER:
                                isEnable[1] = userLocationService.getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                                tvNetworkEnable.setText(": " + isEnable[1]);
                                break;
                            case LocationManager.PASSIVE_PROVIDER:
                                isEnable[2] = userLocationService.getLocationManager().isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                                tvPassiveEnable.setText(": " + isEnable[2]);
                                break;
                        }
                    }
                    updated = true;
                }

                if(!updated)
                    handler.postDelayed(this, 1000); // Same as in displayCurrentLocation function but this time we are gonna stop once it has been updated.
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if(userLocationService != null) {
                        userLocationService.requestLocationUpdates();
                        Log.d("UserLocationActivity", "requested updates");
                    }
                    updateProviderInformation();
                    displayCurrentLocation();
                } else {
                    Log.d("UserLocationActivity", "Permission Denided");
                }
                break;

            default:
                break;
        }
    }
}