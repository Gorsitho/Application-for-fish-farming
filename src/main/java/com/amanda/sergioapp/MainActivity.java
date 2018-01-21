package com.amanda.sergioapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amanda.sergioapp.fragments.MarcadoresActivity;
import com.amanda.sergioapp.fragments.PerfilActivity;
import com.amanda.sergioapp.model.Marcador;
import com.amanda.sergioapp.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference refPersona;
    DatabaseReference refMensaje;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

     Usuario usuario;
    private TextView Header;
    private TextView Nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proximamente...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("[PerfilActivity]", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                }
                // ...
            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        cargarInfo(navigationView);
        setActionBarTitle("SISTEMA DE PECES");
    }
    void cargarInfo(NavigationView a){
        database = FirebaseDatabase.getInstance();
        refPersona = database.getReference("usuario");
        View hview =  a.getHeaderView(0);
        Header = (TextView) hview.findViewById(R.id.usuarioConectado);
        Nombre = (TextView) hview.findViewById(R.id.textView);
        usuario= new Usuario();
        database = FirebaseDatabase.getInstance();
        refPersona = database.getReference("usuario");

        refPersona.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot hijos : dataSnapshot.getChildren()) {

                    System.out.println(hijos.getKey());
                    System.out.println(hijos.getValue());


                }
                usuario = dataSnapshot.getValue(Usuario.class);
                Header.setText(usuario.getNombre()+" "+usuario.getapellidos());
                Nombre.setText(usuario.getcargo());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("[ERROR BASE DE DATOS]: " + databaseError.toString());

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm= getSupportFragmentManager();


        if (id == R.id.nav_perfil) {
            fm.beginTransaction().replace(R.id.content_main, new PerfilActivity()).commit();
        } else if (id == R.id.nav_marcadores) {
            fm.beginTransaction().replace(R.id.content_main, new MarcadoresActivity()).commit();
        } else if (id == R.id.nav_mapa) {
            fm.beginTransaction().replace(R.id.content_main, new MapsActivity()).commit();
        } else if (id == R.id.nav_foto) {
            fm.beginTransaction().replace(R.id.content_main, new subir()).commit();
        } else if (id == R.id.nav_cerrarsesion) {
            Intent intent= new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            mAuth.signOut();


        } else if (id == R.id.nav_send) {
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title){getSupportActionBar().setTitle(title);}
}
