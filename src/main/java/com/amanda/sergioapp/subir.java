package com.amanda.sergioapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amanda.sergioapp.MainActivity;
import com.amanda.sergioapp.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class subir extends Fragment {

    View sView;
     StorageReference mStorage;
    private static final int GALERY_INTENT = 1;
    private ImageView mImagenView;
    private ProgressDialog mProgressDialog;

    public subir() {
        // Required empty public constructor
    }
    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity())
                .setActionBarTitle("Galeria de fotos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sView =inflater.inflate(R.layout.fragment_subir, container, false);
        mStorage= FirebaseStorage.getInstance().getReference();

        ImageView mUploadBtn = (ImageView) sView.findViewById(R.id.subir);
        mImagenView=(ImageView) sView.findViewById(R.id.foto);
        mProgressDialog= new ProgressDialog(getContext());



        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");
                startActivityForResult(intent,GALERY_INTENT);
            }
        });
        return sView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALERY_INTENT && resultCode==-1){

            mProgressDialog.setTitle("Por favor, espere");
             mProgressDialog.setMessage("Actualizando foto");
            mProgressDialog.setCancelable(false);
             mProgressDialog.show();

            Uri uri = data.getData();
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
}
