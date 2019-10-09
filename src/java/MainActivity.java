package com.example.csc8099dissertationproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * MainActivity calls two fragmentActivities , which are OnMapReadyCallback, and POIFragment(actionListener)
 *
 * @author harrietkim
 * @version 01 (2019-08-20)
 */

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, POIFragment.OnListFragmentInteractionListener {


    private static GoogleMap map;
    private UserLocationService userLocationService;
    private boolean bound = false;
    private FragmentManager fragmentManager;
    private SupportMapFragment mapFragment;
    private POIFragment poiFragment;
    private ArrayList<MarkerData> markerDataList;
    private ArrayList<Marker> markerList;

    private int mScreenDensity;

    /**
     * onServiceConnected methods called when a connection to UserLocationService has been established,
     * with the IBinder of the communication channel to the Service.
     * Defines callbacks for service binding, passed to bind Service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Bound to Location service, case iBinder and get Location service instance
            UserLocationService.UserLocationBinder userLocationBinder = (UserLocationService.UserLocationBinder) iBinder;
            userLocationService = userLocationBinder.getBinder();
            bound = true;
        }

        /**
         * onServiceDisConnected methods called when a connection to UserLocationService has been disconnected.
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    private Marker lastUpdatedPOI = null;

    public static void setMapPadding(int height) {
        map.setPadding(0, 0, 0, height);
    }

    /**
     * Function called on application load, checks permissions. Loads location serice.
     */
    @Override
    protected void onStart() {
        super.onStart();

        int check = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        int check2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (check != PackageManager.PERMISSION_GRANTED || check2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // bind to the service
        Intent intent = new Intent(this, UserLocationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * onStop is called when the application is terminated. Unbinds and disconnects the service
     */
    @Override
    protected void onStop() {
        super.onStop();

        // unbind from the service
        if (bound) {
            unbindService(serviceConnection); // service dinconnected
            bound = false;
        }
    }

    /**
     * Creates Fragments and application activities
     * @param savedInstanceState - passes the saved instance to the super.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        mScreenDensity = displaymetrics.densityDpi;
        Log.d("MainActivity", "Screen Density: " + mScreenDensity);

        markerDataList = new ArrayList<>();
        try {
            markerDataList = MarkerParser.getMarkerList(getAssets().open("markerData"), this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();

        //POI Fragment initialisation
        poiFragment = (POIFragment) fragmentManager.findFragmentById(R.id.poiFragment);
        Bundle args = new Bundle();
        args.putInt(POIFragment.ARG_COLUMN_COUNT, 1);
        args.putParcelableArrayList(POIFragment.ARG_POI_LIST, markerDataList);
        poiFragment.initRecyclerView(args);


        // Google Maps Fragment
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    /**
     * Required to draw the map.
     * @param map - pass the googlemap object.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        drawMap();
    }

    //drawing a map and ask permission of userLocation
    public void drawMap() {
        int check = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (check != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {

            // set map style
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = map.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.midnight));

                if (!success) {
                    Log.e("MainActivity", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("MainActivity", "Can't find style.", e);
            }

            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setZoomControlsEnabled(true);
            //map.setPadding(0, 0, 0, 500); // this adds padding to maps UI elements
            map.setMyLocationEnabled(true);

            MarkerInfoWindow customInfoWindow = new MarkerInfoWindow(this);
            map.setInfoWindowAdapter(customInfoWindow);

            // Override onInfoWindowClickListener
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    MarkerData data = (MarkerData) marker.getTag();

                    final String url = data.getWebsite();
                    if (!TextUtils.isEmpty(url)) {
                        final Spannable span = Spannable.Factory.getInstance().newSpannable(url);
                        if (Linkify.addLinks(span, Linkify.WEB_URLS)) {
                            final URLSpan[] old = span.getSpans(0, span.length(), URLSpan.class);
                            if (old != null && old.length > 0) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(old[0].getURL()));
                                startActivity(intent);
                            }
                        }
                    }

                }
            });


            markerList = new ArrayList<>();

            for (MarkerData m : markerDataList) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(m.getTitle());
                markerOptions.snippet(m.getInfo());
                markerOptions.position(m.getLatLng());

                Marker marker = map.addMarker(markerOptions);
                marker.setTag(m);
                markerList.add(marker);
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerDataList.get(0).getLatLng(), 9));
            popupClosestPOI();
            updatePOICardList();
        }
    }

    //Pop up window to display information of point of interests when userLocation getting close to POI
    private void popupClosestPOI() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // get available providers from userlocationservice just once
                if (userLocationService != null && poiFragment != null) {
                    MarkerData closestPOI = userLocationService.getClosestPOI(poiFragment.getPOIData());
                    if (closestPOI == null) { // if null, try again in 2 seconds
                        handler.postDelayed(this, 2000);
                        return;
                    }
                    Marker m = markerList.get(0);
                    for (Marker poi : markerList) {
                        if (poi.getTitle().toLowerCase().trim().equals(closestPOI.getTitle().toLowerCase().trim())) {
                            m = poi;
                        }
                    }

                    // if the closest POI is not the same as the last updated POI then update and show the marker info
                    if (lastUpdatedPOI != m) {
                        lastUpdatedPOI = m;
                        m.showInfoWindow();
                        updatePOICardList();
                    }

                }

                handler.postDelayed(this, 5000); // try updating every 5 seconds
            }
        });
    }

    private void updatePOICardList() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Update recyclerview's markerdata list using userLocationService
                if (userLocationService != null) {
                    userLocationService.updatePOIListByDistance(poiFragment.getPOIData());
                }

                // notify recyclerviewadapter via poifragment
                poiFragment.notifyPOIListUpdate();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permisions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (userLocationService != null) {
                        userLocationService.requestLocationUpdates();
                        Log.d("MainActivity", "requested updates");
                    }
                    drawMap();
                } else {
                    Log.d("MainActivity", "Permission Denided");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(MarkerData item) {
        Log.d("MainActivity", "Recyclerview item " + item.getTitle());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(item.getLatLng(), 15));
        Marker m = markerList.get(0);
        for (Marker poi : markerList) {
            if (poi.getTitle().toLowerCase().trim().equals(item.getTitle().toLowerCase().trim())) {
                m = poi;
            }
        }

        m.showInfoWindow();
    }
}



