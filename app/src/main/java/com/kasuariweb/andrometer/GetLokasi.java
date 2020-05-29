package com.kasuariweb.andrometer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by rail on 7/4/17.
 */

public class GetLokasi {
    private Context context;
    private String latitude="0", longitude="0";
    private Double lat,lon;

    public GetLokasi(Context context) {
        this.context = context;
        prosesLokasi();
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    private void prosesLokasi() {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);

        Boolean isLocated = false;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);

        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();

            this.latitude=lat.toString();
            this.longitude=lon.toString();

            //String lokasi="Lat: "+lat+", Lon: "+lon;
            //Toast.makeText(context, "Lat: "+lat+", Lon: "+lon, Toast.LENGTH_SHORT).show();
            isLocated=true;
        }catch (NullPointerException e){
            //e.printStackTrace();
            //lokasi= null;
            Log.e("ERROR Lokasi",e.toString());
        }catch (Exception e){
            Log.e("ERROR Lokasi",e.toString());
        }
    }
}
