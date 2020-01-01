package com.afeka.blocksEscape;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.annotation.Nullable;



public class LocationFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGooglemap;
    MapView mapView;
    View mView;
    float lat = 30;
    float lng = 30;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_location, container, false);

        return mView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(R.id.map);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGooglemap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
        CameraPosition newLocation = CameraPosition.builder().target(new LatLng(lat, lng)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(newLocation));
    }


    public void placeMarker(String title, float lat, float lon) {
        mGooglemap.clear();
        if (mGooglemap != null) {
            LatLng marker = new LatLng(lat, lon);
            //mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
        CameraPosition newLocation = CameraPosition.builder().target(marker).zoom(16).bearing(0).tilt(45).build();
            mGooglemap.moveCamera(CameraUpdateFactory.newCameraPosition(newLocation));
            mGooglemap.addMarker(new MarkerOptions().title(title).position(marker));
        }
    }



}
