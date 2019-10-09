package com.example.csc8099dissertationproject;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import java.util.Collections;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * UserLocationService binds different types of providers, and updates Last known user location
 *
 *  @author harrietkim
 *  @version 01 (2019-08-20)
 */

public class UserLocationService extends Service {

    class CombinedGPSLocation {
        public Location lastLocation = null;
        public String provider;

        CombinedGPSLocation(){}
    }

    class UserLocationBinder extends Binder {
        UserLocationService getBinder(){
            return UserLocationService.this;
        }
    }

    private UserLocationBinder userLocationBinder = new UserLocationBinder();


    private LocationManager locationManager;
    private List<String> listProviders;
    private static CombinedGPSLocation lastKnownLocation = null;
    private LocationListener locationListener;

    private boolean requested = false;

    void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.d("UserLocationService", "Location Update called");

        if(listProviders.contains("gps"))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        if(listProviders.contains("network"))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if(listProviders.contains("passive"))
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onCreate() {
        lastKnownLocation = new CombinedGPSLocation();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    lastKnownLocation.lastLocation = location;
                    lastKnownLocation.provider = LocationManager.GPS_PROVIDER;
                    Log.d(TAG, "onLocationChanged: Location Changed! GPS");
                }
                if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                    lastKnownLocation.lastLocation = location;
                    lastKnownLocation.provider = LocationManager.NETWORK_PROVIDER;
                    Log.d(TAG, "onLocationChanged: Location Changed! NETWORK");
                }
                if(location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
                    lastKnownLocation.lastLocation = location;
                    lastKnownLocation.provider = LocationManager.PASSIVE_PROVIDER;
                    Log.d(TAG, "onLocationChanged: Location Changed! PASSIVE");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                requestLocationUpdates();
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;
        listProviders = locationManager.getAllProviders();

        requestLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return userLocationBinder;
    }

    Location getLastLocation(String provider) {
        if(lastKnownLocation.provider.equalsIgnoreCase(provider))
            return lastKnownLocation.lastLocation;
        return null;
    }

    Location getLastLocation() {
        return lastKnownLocation.lastLocation;
    }

    MarkerData getClosestPOI(List<MarkerData> pois) {
        double minDistance = Double.MAX_VALUE;
        MarkerData closestPOI = pois.get(0);
        if(lastKnownLocation.lastLocation != null) {
            for (MarkerData poi : pois) {
                Location a = new Location("");
                a.setLongitude(poi.getLatLng().longitude);
                a.setLatitude(poi.getLatLng().latitude);

                double distance = lastKnownLocation.lastLocation.distanceTo(a);
                poi.setDistance(distance);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestPOI = poi;
                }
            }

            return closestPOI;
        }

        return null;
    }

    void updatePOIListByDistance(List<MarkerData> pois) {
        Collections.sort(pois);
    }

    LocationManager getLocationManager() {
        return locationManager;
    }
}
