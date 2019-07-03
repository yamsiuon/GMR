package net.y.y.googlemaprestaurant.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.xml.sax.Locator;

import androidx.core.content.ContextCompat;

public class LocationUtility implements LocationListener {

    private final int TIME_INTERVAL = 10000;
    private final int DISTANCE_INTERVAL = 10;

    public enum Method {
        NETWORK,
        GPS,
        NETWORK_THEN_GPS
    }

    private Context context;
    private LocationManager locationManager;
    private LocationUtility.Method method;
    private LocationUtility.Listener callback;

    public LocationUtility(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getLocation(LocationUtility.Method method, LocationUtility.Listener callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.method = method;
            this.callback = callback;
            switch (this.method) {
                case NETWORK:
                case NETWORK_THEN_GPS:
                    Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (networkLocation != null) {
                        this.callback.onLocationFound(networkLocation);
                    }
//                    else {
                    this.requestUpdates(LocationManager.NETWORK_PROVIDER);
//                    }
                    break;
                case GPS:
                    Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        this.callback.onLocationFound(gpsLocation);
                    }
//                    else {
                    this.requestUpdates(LocationManager.GPS_PROVIDER);
//                    }
                    break;
            }
        }
    }

    private void requestUpdates(String provider) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else {
                onProviderDisabled(provider);
            }
        } else {

            onProviderDisabled(provider);
        }
    }

    public void cancel() {
        this.locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
//        this.locationManager.removeUpdates(this);
        this.callback.onLocationFound(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (method == LocationUtility.Method.NETWORK_THEN_GPS
                && provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
            requestUpdates(LocationManager.GPS_PROVIDER);
        } else {
            locationManager.removeUpdates(this);
            callback.onLocationNotFound();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public interface Listener {
        void onLocationFound(Location location);

        void onLocationNotFound();
    }

    public float getTwoPointDistance(
            double lat1,
            double lng1,
            double lat2,
            double lng2
    ) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);

        return results[0];
    }
}
