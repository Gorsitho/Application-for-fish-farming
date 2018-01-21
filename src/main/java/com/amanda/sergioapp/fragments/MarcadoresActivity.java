package com.amanda.sergioapp.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amanda.sergioapp.MainActivity;
import com.amanda.sergioapp.MapsActivity;
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

import com.amanda.sergioapp.model.Marcador;
import com.amanda.sergioapp.R;
import com.google.firebase.database.ValueEventListener;


public class MarcadoresActivity extends Fragment  implements OnMapReadyCallback {
    private static final int MY_PERMISSION_FINE_LOCATION =101 ;
    MapView mMapView;
    GoogleMap mGoogleMap;
    Button btnGuardar;

    EditText txtLatitud;
    EditText txtLongitud;
    EditText txtNombre;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refMarcador = database.getReference("marcadores");
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity())
                .setActionBarTitle("Estanques marcados");
    }
    public MarcadoresActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_marcadores, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGuardar = view.findViewById(R.id.btnGuardar);
        txtLatitud = view.findViewById(R.id.txtLatitud);
        txtLongitud = view.findViewById(R.id.txtLongitud);
        txtNombre = view.findViewById(R.id.txtNombreMarcador);
        btnGuardar = view.findViewById(R.id.btnGuardar);


        mMapView =(MapView) view.findViewById(R.id.mapaMarcador);

        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marcador marcador = new Marcador();
                if (txtLatitud.getText().toString().trim().isEmpty()||txtLongitud.getText().toString().trim().isEmpty()||txtNombre.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(),"Error: Hay un campo vacio", Toast.LENGTH_SHORT).show();
                    return;}
                marcador.setLatitud(Double.parseDouble(txtLatitud.getText().toString().trim()));
                marcador.setLongitud(Double.parseDouble(txtLongitud.getText().toString().trim()));
                marcador.setNombre(txtNombre.getText().toString().trim());

                refMarcador.child(marcador.getNombre()).setValue(marcador);
                Toast.makeText(view.getContext(), "Marcador guardado correctamente!", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MarcadoresActivity.this.getContext(),""+ mGoogleMap.getMyLocation().getLatitude()+" , "+mGoogleMap.getMyLocation().getLongitude(), Toast.LENGTH_SHORT).show();
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

        if (ActivityCompat.checkSelfPermission(MarcadoresActivity.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
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
                    if (ActivityCompat.checkSelfPermission(MarcadoresActivity.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(MarcadoresActivity.this.getContext(), "Esta app requiere permiso de gps", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}
