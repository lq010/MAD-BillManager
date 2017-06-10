package com.mobile.madassignment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.mobile.madassignment.util.Img;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class FullImgActivity extends AppCompatActivity {
    private  ImageView iv_preview_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_img);
        iv_preview_image = (ImageView)findViewById(R.id.iv_preview_image);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.info_group_toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
        iv_preview_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullImgActivity.this.finish();
            }
        });
        Intent intent = getIntent();

        String group_key = intent.getStringExtra("groupKey");
        String path = intent.getStringExtra("photoName");
        Log.d("photoName",path);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        ImageView im = (ImageView)findViewById(R.id.iv_preview_image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
        StorageReference photoRef = storageRef.child(group_key).child(path);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    Picasso.with(FullImgActivity.this).load(localFile).into(iv_preview_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(FullImgActivity.this,"Internet error",Toast.LENGTH_SHORT);
                    int errorCode = ((StorageException) exception).getErrorCode();
                    String errorMessage = exception.getMessage();
                    Log.d("userPhoto", errorMessage);
                }
            });
        } catch (IOException e) {
            Log.d("userPhoto", e.getMessage());
        }
    }
}
