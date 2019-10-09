package com.example.csc8099dissertationproject;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * MarkerData holds all of the data for the marker objects. Implements Parcelable and Comparable.
 *
 * @author harrietkim
 * @version 01 (2019-08-20)
 */

public class MarkerData implements Parcelable, Comparable<MarkerData> {

    public static final Creator<MarkerData> CREATOR = new Creator<MarkerData>() {
        @Override
        public MarkerData createFromParcel(Parcel in) {
            return new MarkerData(in);
        }

        @Override
        public MarkerData[] newArray(int size) {
            return new MarkerData[size];
        }
    };

    private String title;
    private String info;
    private String address;
    private String website;
    private LatLng latLng;
    private Bitmap image;
    private double distance;

    /**
     * Standard initialisation of Marker Object.
     * @param title
     * @param info
     * @param address
     * @param website
     * @param latLng
     * @param image
     */
    public MarkerData(String title, String info, String address, String website, LatLng latLng, Bitmap image) {
        this.title = title;
        this.info = info;
        this.address = address;
        this.website = website;
        this.latLng = latLng;
        this.image = image;
        this.distance = 0;
    }

    /**
     * Parcel object method of initialisation of Marker Object.
     * @param in
     */
    protected MarkerData(Parcel in) {
        title = in.readString();
        info = in.readString();
        address = in.readString();
        website = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        image = in.readParcelable(Bitmap.class.getClassLoader());
        distance = in.readDouble();
    }

    @Override
    public String toString() {
        return "MarkerData{" +
                "title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                ", latLng=" + latLng +
                ", image=" + image +
                ", distance=" + distance +
                '}';
    }

    /**
     * Getter and Setter methods
     *
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() { return info; }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) { this.address = address; }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Bitmap getImage() { return image; }

    public void setImage(Bitmap image) { this.image = image; }

    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(info);
        parcel.writeString(address);
        parcel.writeString(website);
        parcel.writeParcelable(latLng, i);
        parcel.writeParcelable(image, i);
        parcel.writeDouble(distance);
    }

    // negative = less than
    // zero = equal
    // positive = bigger

    /**
     * Required method from Comparable interface.
     * @param markerData
     * @return
     */
    @Override
    public int compareTo(MarkerData markerData) {
        return Double.compare(this.distance, markerData.distance);
    }
}
