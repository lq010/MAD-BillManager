package com.mobile.madassignment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class UserProfileActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        update_picture = (Button)findViewById(R.id.bt_update_picture);
        update_profile = (Button)findViewById(R.id.bt_update_profile);
        back = (ImageView)findViewById(R.id.iv_user_profile_back);
        nickname = (EditText)findViewById(R.id.user_nickname);
        password = (EditText)findViewById(R.id.user_password);
        repeatPW = (EditText)findViewById(R.id.user_password_repeat);
        picture = (ImageView)findViewById(R.id.picture);
        picture_url = (EditText)findViewById(R.id.picture_url);
        emailAddress = (TextView)findViewById(R.id.user_email);
        change_password = (Button)findViewById(R.id.bt_change_paasword);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileActivity.this.finish();
            }
        });

        final String name = user.getDisplayName();
        String emailstr = user.getEmail();
        Uri photo = user.getPhotoUrl();

        //Toast.makeText(UserProfileActivity.this, "url:" + photo.toString(),Toast.LENGTH_SHORT ).show();
        if(photo != null) {
            DownImage PrintImage = new DownImage(picture, photo.toString());
            PrintImage.execute();
        }
        nickname.setText(name);
        emailAddress.setText(emailstr);

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
                                        Toast.makeText(UserProfileActivity.this, "profile update success.",Toast.LENGTH_SHORT ).show();
                                        UserProfileActivity.this.finish();
                                    }
                                    else {
                                        Toast.makeText(UserProfileActivity.this, "profile update failed.",Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            });
                }
            }
        });

        update_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlStr = picture_url.getText().toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(urlStr))
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.v("update profile", "User picture updated.");
                                    Toast.makeText(UserProfileActivity.this, "picture update success.",Toast.LENGTH_SHORT ).show();
                                    UserProfileActivity.this.finish();
                                }
                                else {
                                    Toast.makeText(UserProfileActivity.this, "picture update failed.",Toast.LENGTH_SHORT ).show();
                                }
                            }
                        });
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                boolean cancel = false;
                String passwordStr = password.getText().toString();
                String repeatStr = repeatPW.getText().toString();
                if (TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
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
                                        mAuth.signOut();
                                    }
                                }
                            });
                }
            }
        });
    }


//    public Bitmap getURLimage(String url) {
//        Bitmap bmp = null;
//        try {
//            URL myurl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
//            conn.setConnectTimeout(6000);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            bmp = BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bmp;
//    }




    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isPasswordRepeatedCorrectly(String password, String repeat) {
        return password.equals(repeat);
    }

}

class DownImage extends AsyncTask {
    private ImageView imageView;
    private String url;
    public DownImage(ImageView imageView, String url) {
        this.imageView = imageView;
        this.url = url;
    }

    protected void onPostExecute(Object result) {
        imageView.setImageBitmap((Bitmap)result);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
