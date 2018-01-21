package com.amanda.sergioapp.fragments;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amanda.sergioapp.Login;
import com.amanda.sergioapp.MainActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.amanda.sergioapp.SplashActivity;
import com.amanda.sergioapp.model.Usuario;
import com.amanda.sergioapp.R;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class PerfilActivity extends Fragment{

    View sView;
    StorageReference mStorage;
    private static final int GALERY_INTENT = 1;
    private ImageView mImagenView;
    private ImageView imageView;
    private ProgressDialog mProgressDialog;

    FirebaseDatabase database;
    Button btnRegistrar;
    DatabaseReference refPersona;
    DatabaseReference refMensaje;

    Usuario usuario;
    Button btnCerrarSesion;
    EditText txtNombre;
    EditText txtApellidos;
    EditText txtCargo;
    EditText txtPeso;
    EditText txtAltura;
    EditText txtFechaNacimiento;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //  mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public PerfilActivity() {
        // Required empty public constructor

    }

    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity())
                .setActionBarTitle("Perfil empleado");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        sView =inflater.inflate(R.layout.fragment_perfil, container, false);
        mStorage= FirebaseStorage.getInstance().getReference();

        ImageView mUploadBtn = (ImageView) sView.findViewById(R.id.foto);
        mImagenView=(ImageView) sView.findViewById(R.id.foto);
        imageView=(ImageView) sView.findViewById(R.id.imageView);
        mProgressDialog= new ProgressDialog(getContext());




        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Selecciona una galeria"), GALERY_INTENT);


            }
        });
        return sView;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                    Intent intent= new Intent(getContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                // ...
            }


        };

        usuario = new Usuario();
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);


        txtNombre = view.findViewById(R.id.txtNombre);
        txtApellidos=view.findViewById(R.id.txtApellidos);
        txtCargo=view.findViewById(R.id.txtCargo);
        txtPeso = view.findViewById(R.id.txtPeso);
        txtAltura = view.findViewById(R.id.txtAltura);
        txtFechaNacimiento = view.findViewById(R.id.txtFechaNacimiento);

        database = FirebaseDatabase.getInstance();
        refPersona = database.getReference("usuario");
        refMensaje = database.getReference("mensaje");


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                if( txtPeso.getText().toString().trim().isEmpty() ||  txtAltura.getText().toString().trim().isEmpty() ||
                        txtFechaNacimiento.getText().toString().trim().isEmpty() || txtNombre.getText().toString().trim().isEmpty()
                        || txtCargo.getText().toString().trim().isEmpty() || txtApellidos.getText().toString().trim().isEmpty()){
                Toast.makeText(getContext(),"Error: Hay un campo vacio", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(),"Registro Exitoso!", Toast.LENGTH_SHORT).show();
                usuario.setPeso(txtPeso.getText().toString().trim());
                usuario.setAltura(txtAltura.getText().toString().trim());
                usuario.setFechaNacimiento(txtFechaNacimiento.getText().toString().trim());
                usuario.setNombre(txtNombre.getText().toString().trim());
                usuario.setapellidos(txtApellidos.getText().toString().trim());
                usuario.setcargo(txtCargo.getText().toString().trim());





                refPersona.child(mAuth.getCurrentUser().getUid()).setValue(usuario);

            }
        });



                btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signOut();
                    }
                });

        cargarInfo();

            }



            void cargarInfo(){

                refPersona.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot hijos : dataSnapshot.getChildren()) {

                            System.out.println(hijos.getKey());
                            System.out.println(hijos.getValue());


                        }
                        usuario = dataSnapshot.getValue(Usuario.class);
                        txtNombre.setText(usuario.getNombre());
                        txtPeso.setText(usuario.getPeso());
                        txtFechaNacimiento.setText(usuario.getFechaNacimiento());
                        txtAltura.setText(usuario.getAltura());
                        txtCargo.setText(usuario.getcargo());
                        txtApellidos.setText(usuario.getapellidos());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("[ERROR BASE DE DATOS]: " + databaseError.toString());

                    }
                });
            }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         Uri uri = data.getData();

        //cargarImagen(uri.getLastPathSegment());
        if (requestCode==GALERY_INTENT && resultCode==-1){

            mProgressDialog.setTitle("Por favor, espere");
            mProgressDialog.setMessage("Actualizando foto");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();



            StorageReference filePath =mStorage.child("fotos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgressDialog.dismiss();

                    Uri descargarFoto = taskSnapshot.getDownloadUrl();

                    Glide.with(getActivity())
                            .load(descargarFoto)
                            .fitCenter()
                            .centerCrop()
                            .into(mImagenView);


                    Toast.makeText(getActivity(), "Se subio exitosamente la foto", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void cargarImagen(String imagen){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://danielapp-f0f9a.appspot.com/fotos").child("fotoperfil");
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    mImagenView.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}




    }



}
