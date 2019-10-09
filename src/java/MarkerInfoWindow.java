package com.example.csc8099dissertationproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * MarkerInfoWindow class implements the Google Info-Windows. Allows the creation and retrival of the views.
 *
 * @author harrietkim
 * @version 01 (2019-08-20)
 */

public class MarkerInfoWindow implements GoogleMap.InfoWindowAdapter {
    private Context context;


    public MarkerInfoWindow(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * Displaying Marker items to the View (Info-Window)
     * @param marker
     * @return view
     */
    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.marker_info_window, null);

        TextView title_tv = view.findViewById(R.id.title);
        TextView info_tv = view.findViewById(R.id.info);
        TextView address_tv = view.findViewById(R.id.address);
        TextView website_tv = view.findViewById(R.id.website);
        TextView position_tv = view.findViewById(R.id.latlng);

        title_tv.setText(marker.getTitle());
        MarkerData markerData = (MarkerData) marker.getTag();
        info_tv.setText(markerData.getInfo());
        address_tv.setText(markerData.getAddress());

        android.text.Spanned html_text;
        if (Build.VERSION.SDK_INT >= 24) {
            html_text = Html.fromHtml("<a href=\"" + markerData.getWebsite() + "\">" + markerData.getWebsite() + "</a>", Html.FROM_HTML_MODE_LEGACY);
        } else {
            html_text = Html.fromHtml("<a href=\"" + markerData.getWebsite() + "\">" + markerData.getWebsite() + "</a>");
        }
        website_tv.setText(html_text);
        website_tv.setMovementMethod(LinkMovementMethod.getInstance());
        website_tv.setLinkTextColor(Color.parseColor("#1976d2"));

        position_tv.setText(markerData.getLatLng().toString());
        return view;
    }
}
