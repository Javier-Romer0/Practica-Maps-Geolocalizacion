package cat.itb.practicamaps.models;

import android.graphics.drawable.Icon;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

public class LocationMarker {
    private String idMarker;
    private String title;
    private double lat;
    private double lon;
    private String pictureurl;
    private String description;
    private BitmapDescriptor icon;

    public LocationMarker() {
    }

    public LocationMarker(String idMarker, String title, double lat, double lon, String description, String pictureurl) {
        this.idMarker = idMarker;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
        this.pictureurl = pictureurl;
    }

    public String getIdMarker() {
        return idMarker;
    }

    public void setIdMarker(String idMarker) {
        this.idMarker = idMarker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }
}
