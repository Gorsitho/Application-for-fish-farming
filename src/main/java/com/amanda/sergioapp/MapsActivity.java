package com.amanda.sergioapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amanda.sergioapp.model.Marcador;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsActivity extends Fragment implements OnMapReadyCallback {
Location locacion;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity())
                .setActionBarTitle("Mapa");
    }


    public MapsActivity() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView =(MapView) view.findViewById(R.id.mapa);

        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap= googleMap;

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this.getContext(),""+ mGoogleMap.getMyLocation().getLatitude()+" , "+mGoogleMap.getMyLocation().getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });

        UiSettings uiSettings = mGoogleMap.getUiSettings();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refMarcador = database.getReference("marcadores");

        refMarcador.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot hijo : dataSnapshot.getChildren()){

                    System.out.println(hijo.getKey().toString());
                    Marcador marcador= hijo.getValue(Marcador.class);

                    LatLng posicion = new LatLng(marcador.getLatitud(), marcador.getLongitud());
                    //LatLng posicion = new LatLng(locacion.getLatitude(), locacion.getLongitude());

                    mGoogleMap.addMarker(new MarkerOptions().position(posicion).title(marcador.getNombre()));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 16));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (ActivityCompat.checkSelfPermission(MapsActivity.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mGoogleMap.setMyLocationEnabled(true);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
        uiSettings.setMyLocationButtonEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(MapsActivity.this.getContext(), "Esta app requiere permiso de gps", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}

