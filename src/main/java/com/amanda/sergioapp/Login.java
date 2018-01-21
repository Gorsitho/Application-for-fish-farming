package com.amanda.sergioapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.amanda.sergioapp.model.Usuario;
import com.amanda.sergioapp.R;

import java.io.File;

import jp.wasabeef.blurry.Blurry;


public class Login extends AppCompatActivity {

    private EditText txtUsuario;
    private EditText txtClave;
    private Button btnRegistrar;
    private Button btnLogin;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtClave = (EditText) findViewById(R.id.txtClave);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("[Login]", "Actualmente se encuentra logueado: " + user.getUid());
                    System.out.println("Email verificado: " + mAuth.getCurrentUser().isEmailVerified());
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        System.out.println("Entra al  listener");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        finish();

                    } else {
                        System.out.println("no entra al listener");
                    }

                } else {
                    // User is signed out
                    Log.d("", "No hay usuario logueado");
                }
                // ...
            }
        };


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( txtUsuario.getText().toString().trim().isEmpty() ||  txtClave.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Error: Hay un campo vacio", Toast.LENGTH_SHORT).show();
                    return;}
                mAuth.createUserWithEmailAndPassword(txtUsuario.getText().toString().trim(), txtClave.getText().toString().trim())
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Log.d("[Creacion]", "Registro de usuario: " + task.isSuccessful() +" --- "+task.getException());

                                String mensaje="";
                                if (task.isSuccessful()) {

                                    mensaje="Usuario creado correctamente " + mAuth.getCurrentUser().getUid();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    DatabaseReference refUsuario = database.getReference("usuario");

                                    Usuario usuario = new Usuario();

                                    usuario.setFechaNacimiento("");
                                    usuario.setNombre("");
                                    usuario.setAltura("");
                                    usuario.setPeso("");

                                    refUsuario.child(mAuth.getCurrentUser().getUid()).setValue(usuario);

                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(Login.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(Login.this,"Email de verificación enviado :)", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(Login.this,"Email de verificación No enviado :(", Toast.LENGTH_SHORT).show();
                                            }
                                            mAuth.signOut();
                                        }
                                    });


                                }else{
                                    mensaje="Usuario no creado correctamente ";

                                }


                                Toast.makeText(Login.this, mensaje,
                                        Toast.LENGTH_SHORT).show();

                            }
                        });



            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( txtUsuario.getText().toString().trim().isEmpty() ||  txtClave.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Error: Hay un campo vacio", Toast.LENGTH_SHORT).show();
                    return;}

                mAuth.signInWithEmailAndPassword(txtUsuario.getText().toString().trim(), txtClave.getText().toString().trim())
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Log.d("[Login]", "Usuario logueado: " + task.isSuccessful() +" ---- "+task.getException());

                                String mensaje="";
                                if (task.isSuccessful()) {

                                    mensaje="Usuario Logueado correctamente "+ mAuth.getCurrentUser().getUid();
//----------------------------------
                                    if(mAuth.getCurrentUser().isEmailVerified()){

                                        Intent intent= new Intent(Login.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        Toast.makeText(Login.this, "Email no verificado", Toast.LENGTH_SHORT).show();
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(Login.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    Toast.makeText(Login.this,"Email de verificación enviado nuevamente", Toast.LENGTH_SHORT).show();
                                                }
                                                mAuth.signOut();
                                            }
                                        });
                                    }


                                }else{
                                    mensaje="Usuario no Logueado correctamente ";

                                }

                                Toast.makeText(Login.this, mensaje,
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //mAuth.removeAuthStateListener(mAuthListener);
        }
    }



}
