package com.mobile.madassignment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static com.mobile.madassignment.util.Constants.Node_userInfo;

public class UserProfileActivity extends Activity {

    private Button gallery;
    private Button camera;
    private Button update_picture;
    private Button update_profile;
    private ImageView back;
    private EditText nickname;
    private EditText password;
    private EditText repeatPW;
    private ImageView picture;
    private EditText picture_url;
    private TextView emailAddress;
    private Button change_password;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mRootRef;

    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private Bitmap tempPic;

    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    private boolean picture_choosed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        gallery = (Button)findViewById(R.id.photo_from_gallery);
        //camera = (Button)findViewById(R.id.photo_from_camera);
        update_picture = (Button)findViewById(R.id.bt_update_picture);
        update_profile = (Button)findViewById(R.id.bt_update_profile);
        back = (ImageView)findViewById(R.id.iv_user_profile_back);
        nickname = (EditText)findViewById(R.id.user_nickname);
        password = (EditText)findViewById(R.id.user_password);
        repeatPW = (EditText)findViewById(R.id.user_password_repeat);
        picture = (ImageView)findViewById(R.id.picture);
        emailAddress = (TextView)findViewById(R.id.user_email);
        change_password = (Button)findViewById(R.id.bt_change_paasword);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();

                setResult(Activity.RESULT_OK,result);

                UserProfileActivity.this.finish();
            }
        });

        final String name = user.getDisplayName();
        String emailstr = user.getEmail();


        //Uri photo = user.getPhotoUrl();

        //Toast.makeText(UserProfileActivity.this, "url:" + photo.toString(),Toast.LENGTH_SHORT ).show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
        StorageReference photoRef = storageRef.child(user.getUid()+".jpg");

        try {
            final File localFile = File.createTempFile("images", "jpg");
            final long ONE_MEGABYTE = 1024*1024;
            photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Picasso.with(UserProfileActivity.this).load(localFile).into(picture);
                    //Toast.makeText(UserProfileActivity.this, "photo download success",Toast.LENGTH_SHORT ).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    //Toast.makeText(UserProfileActivity.this, "photo download failed",Toast.LENGTH_SHORT ).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        nickname.setText(name);
        emailAddress.setText(emailstr);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on_gallery_click();
            }
        });
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                on_camera_click();
//            }
//        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nicknameStr = nickname.getText().toString();
                if(nicknameStr.equals(name)) {
                    Toast.makeText(UserProfileActivity.this, "The nickname is not changed.",Toast.LENGTH_SHORT ).show();
                } else {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nicknameStr)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.v("update profile", "User profile updated.");
                                        //Toast.makeText(UserProfileActivity.this, "profile update success.",Toast.LENGTH_SHORT ).show();
                                        UserProfileActivity.this.finish();
                                    }
                                    else {
                                        //Toast.makeText(UserProfileActivity.this, "profile update failed.",Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            });
                }
            }
        });

        update_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(picture_choosed) {
                    picture.setDrawingCacheEnabled(true);
                    picture.buildDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    tempPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
                    StorageReference photoRef = storageRef.child(user.getUid() + ".jpg");
                    UploadTask uploadTask = photoRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(UserProfileActivity.this, "Photo update failed.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Toast.makeText(UserProfileActivity.this, "Photo update success.", Toast.LENGTH_SHORT).show();
                            mRootRef.child("users").child(user.getUid()).child("photoFile").setValue(user.getUid());
                            mRootRef.child("users").child(user.getUid()).child(Node_userInfo).child("profilePhoto").setValue(user.getUid());
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });
                }
                else {
                    Toast.makeText(UserProfileActivity.this, "Please choose a new photo before upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                boolean cancel = false;
                String passwordStr = password.getText().toString();
                String repeatStr = repeatPW.getText().toString();
                if (TextUtils.isEmpty(passwordStr)) {
                    password.setError(getString(R.string.error_invalid_password));
                    focusView = password;
                    cancel = true;
                }
                if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
                    password.setError(getString(R.string.error_invalid_password));
                    focusView = password;
                    cancel = true;
                }

                if(!isPasswordRepeatedCorrectly(passwordStr, repeatStr)) {
                    repeatPW.setError(getString(R.string.error_repeat));
                    focusView = repeatPW;
                    cancel = true;
                }
                if(cancel) {
                    focusView.requestFocus();
                } else {
                    user.updatePassword(passwordStr)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.v("update profile", "User password updated.");
                                        Toast.makeText(UserProfileActivity.this, "password update success.",Toast.LENGTH_SHORT ).show();
                                        UserProfileActivity.this.finish();
                                    }
                                    else {
                                        Toast.makeText(UserProfileActivity.this, "For sicurity, You need to log in first.",Toast.LENGTH_SHORT ).show();
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        mRootRef.child("users").child(mAuth.getCurrentUser().getUid()).child("deviceTokens")
                                                .child(deviceToken).setValue(false);
                                        mAuth.signOut();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void on_gallery_click() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    public void on_camera_click() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }
    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //size
        intent.putExtra("outputX", 125);
        intent.putExtra("outputY", 140);

        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(UserProfileActivity.this, "SDCard nod found,no space for photo", Toast.LENGTH_SHORT ).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                tempPic = data.getParcelableExtra("data");
                picture.setImageBitmap(tempPic);
                picture_choosed = true;
            }
            try {
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isPasswordRepeatedCorrectly(String password, String repeat) {
        return password.equals(repeat);
    }
}
