package com.example.csc8099dissertationproject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * JSON file parsing, markerData JSON via assets (JSON file) parse to JSON objects
 *
 * @author harrietkim
 * @version 01 (2019-08-20)
 */

public class MarkerParser {

    /**
     * Returns a ArrayList of Marker objects, taking in an InputSteam and context.
     * @param is refers to Input data from markerData.file(JSON)
     * @param context
     * @return markerList
     * @throws Exception
     */
    public static ArrayList<MarkerData> getMarkerList(InputStream is, Context context) throws Exception {
        ArrayList<MarkerData> markerList = new ArrayList<>();

            byte[] buffer = new byte[is.available()];

            is.read(buffer);

            is.close();

            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);


            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                /**
                 * Directly parsing
                 */
                String title = obj.getString("title");
                String information = obj.getString("information");
                String address = obj.getString("address");
                String website = obj.getString("website");
                JSONObject latlngObj = obj.getJSONObject("latLng");
                String imageTitle = obj.getString("image");

                int id = context.getResources().getIdentifier(imageTitle, "drawable", context.getPackageName());
                Bitmap image = decodeSampledBitmapFromResource(context.getResources(), id, 128, 256);// BitmapFactory.decodeResource(context.getResources(), id);

                // latLng
                double lat = latlngObj.getDouble("lat");
                double lng = latlngObj.getDouble("lng");

                markerList.add(new MarkerData(title, information, address, website, new LatLng(lat, lng), image));
            }

        return markerList;

    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
