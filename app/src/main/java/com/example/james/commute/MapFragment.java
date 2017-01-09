package com.example.james.commute;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by james on 12/23/2016.
 */

public class MapFragment extends Fragment {
    String stopname;
    LocCord stopLoc;
    LocCord uLoc;
    MapView mMapView;
    private GoogleMap googleMap;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflator.inflate(R.layout.maps, container, false);

        Bundle bundle = new Bundle();
        bundle = this.getArguments();

        if(bundle!=null){
            stopLoc = new LocCord(bundle.getDouble("blon"),bundle.getDouble("blat"));
            uLoc = new LocCord(bundle.getDouble("ulon"),bundle.getDouble("ulat"));
            stopname= bundle.getString("stopname");
        }


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        //to display map immediately
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception E) {
            E.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                //to show moving to your location
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                MarkerOptions userMarker = new MarkerOptions();
                userMarker.position(new LatLng(uLoc.getLat(),uLoc.getLon()));
                LatLng busStopLoc = new LatLng(stopLoc.getLat(),stopLoc.getLon());
                MarkerOptions busStopMarker = new MarkerOptions().position(busStopLoc).title(stopname+" stop");
                //dropping marker on the map

                googleMap.addMarker(busStopMarker);
                //To autozoom properly
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(userMarker.getPosition());
                builder.include(busStopMarker.getPosition());

                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int)(width*0.40);
                //camera position
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(busStopLoc).zoom(15).build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);

                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.animateCamera(cu);


            }
        });
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
