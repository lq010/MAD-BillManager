package com.mobile.madassignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mobile.madassignment.models.UserInfo;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mobile.madassignment.util.Constants.Default_Photo;
import static com.mobile.madassignment.util.Constants.Node_userInfo;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int PROFILE_SETTING = 105;
    private static final int Logout = 1000001;
    private static final int UPDATE_PROFILE = 10001;
    private static final int RC_SIGN_IN = 11000;
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private Map<Long, String> menuId_group;
    long id_counter = 0;

    //Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean AuthState = false;
    private String userName = "login/register";
    private String userAddress = "XXX@XXXXX.XX";
    private Toolbar toolbar;
    private MainFragment mainFragment;
    //private Uri photoUrl;
    //private String UID = "";

    private GoogleApiClient mGoogleApiClient;

    private boolean isGroupDrawerInited = false;

    public Toolbar getToolbar() {
        return toolbar;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final IProfile defaultprofile = new ProfileDrawerItem().withName("Batman").withEmail("batman@gmail.com")
                .withIcon(R.drawable.profile).withIdentifier(PROFILE_SETTING);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    mRootRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean init = true;
                            for (DataSnapshot data :dataSnapshot.getChildren()){
                                if (data.getKey().matches(Node_userInfo))
                                    init = false;
                            }
                            if(init){
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                mRootRef.child("users").child(user.getUid()).child("deviceTokens")
                                        .child(deviceToken).setValue(true);
                                String userPhoto = Default_Photo;
                                UserInfo userInfo = new  UserInfo(user.getUid(),user.getDisplayName(),user.getEmail(),userPhoto) ;
                                mRootRef.child("users").child(user.getUid()).child(Node_userInfo).setValue(userInfo);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Log.v( "starting fragment", "onAuthStateChanged:signed_in:" + user.getUid());

                    if(!isGroupDrawerInited){

                        initGroupList(user);
                    }


                } else {
                    headerResult.updateProfile(defaultprofile);

                    result.removeAllItems();
                    menuId_group.clear();
                    isGroupDrawerInited =false;

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);

                }
                // ...

            }
        };



        menuId_group = new ConcurrentHashMap<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);



        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()

                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                //.withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        defaultprofile,
                        new ProfileSettingDrawerItem().withName("sign out").
                                withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(Logout)
                )

                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                   //sign out
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == Logout) {
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            if(mAuth.getCurrentUser()!=null) {
                                mRootRef.child("users").child(mAuth.getCurrentUser().getUid()).child("deviceTokens")
                                        .child(deviceToken).setValue(false);

                                AuthUI.getInstance()
                                        .signOut(MainActivity.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // user is now signed out
                                                showSnackbar("signed out");
                                                try{
                                                FragmentManager fm = getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                ft.remove(fm.findFragmentById(R.id.main_content));
                                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                                                ft.commit();}
                                                catch (NullPointerException e){

                                                }

                                            }
                                        });

                            }
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            //Toast.makeText(MainActivity.this, "click on profile setting", Toast.LENGTH_SHORT).show();
                            if(mAuth.getCurrentUser()!=null){
                                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                                startActivityForResult(intent, UPDATE_PROFILE);
                            }

                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .build();


        //drawer items
        result = new DrawerBuilder()
                .withPositionBasedStateManagement(true)
                .withActivity(this)
                .withStickyFooter(R.layout.drawer_footer)
                .withFooterClickable(true)
                .withToolbar(toolbar)
                .withShowDrawerOnFirstLaunch(true)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        Log.v("clicked ",position+"");

                        if (drawerItem != null) {
                            long id = drawerItem.getIdentifier();
                            String group_key = menuId_group.get(id);
                            Log.v("starting fragment", "item id:"+id +" group_key:"+ group_key);
                            // Toast.makeText(MainActivity.this, id + group_key, Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("group_key", group_key);

                            mainFragment = new MainFragment();
                            FragmentManager manager = getSupportFragmentManager();

                            mainFragment.setArguments(bundle);
                            manager.beginTransaction().replace(R.id.main_content,mainFragment).commit();

                        }
                        return false;

                    }
                })

                .withSavedInstance(savedInstanceState)
                .build();


        result.getStickyFooter().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("foot_id",view.getId()+"");

                Intent intent = new Intent(MainActivity.this,AddGroupActivity.class);
                startActivity(intent);
            }
        });

        if(!isInternetAvailable(this.getBaseContext())){
            Toast.makeText(this, "Please connect to the Internet",Toast.LENGTH_SHORT ).show();
        }

        //if intent.getExtras != null, get the group key from intent, start fragment
        if (getIntent().getExtras() != null) {
            for(String key : getIntent().getExtras().keySet()){
                if(key.matches("group_key")){
                    Log.d("notification_groupkey",getIntent().getExtras().get("group_key").toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("group_key", getIntent().getExtras().get("group_key").toString());

                    result.closeDrawer();
                    MainFragment mainFragment = new MainFragment();
                    FragmentManager manager = getSupportFragmentManager();

                    mainFragment.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.main_content,mainFragment).commit();

                }
            }

        }

    //firebase invites
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d("MainActivity", "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });


    }




    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
//check Internet connection
    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return  isConnected;

    }

//get groups
    private void initGroupList(final FirebaseUser user){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
        StorageReference photoRef = storageRef.child(user.getUid()+".jpg");

        final IProfile profile = new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail())
                .withIcon(R.drawable.profile).withIdentifier(PROFILE_SETTING);
        headerResult.updateProfile(profile);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            final long ONE_MEGABYTE = 1024*1024;
            photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    profile.withIcon(BitmapFactory.decodeFile(localFile.getPath()));
                    headerResult.updateProfile(profile);
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
            Log.d("MainActivity",e.getMessage());
        }

        final DatabaseReference groups = mRootRef.child("groups");
        DatabaseReference user_groups = mRootRef.child("users").child(user.getUid()).child("groups");

        user_groups.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final String groupKey = dataSnapshot.getKey();
                Log.v("groupId ",groupKey);
                 DatabaseReference group = groups.child(groupKey);
                    group.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String groupName =  dataSnapshot.child("name").getValue().toString();
                            int num_members = (int)dataSnapshot.child("members").getChildrenCount();
                            Log.v("retrieve from groups", "name = " +groupName+", members : "+num_members);
                            if(!menuId_group.containsValue(groupKey)){
                                Log.v("contains","false, add new item");
                                Log.v("add new item", "id= " +id_counter);
                                result.addItem(
                                        new PrimaryDrawerItem()
                                                .withName(groupName)
                                                .withDescription(num_members + " members")
                                                .withDescriptionTextColor(Color.parseColor("#b4b6ba"))
                                                .withIcon(R.drawable.profile)
                                                .withIdentifier(id_counter)
                                                .withSelectable(true)
                                                .withBadgeStyle(new BadgeStyle()
                                                        .withTextColor(Color.WHITE)
                                                        .withColorRes(R.color.md_red_700))
                                );
                                menuId_group.put(id_counter,groupKey);
                                ++id_counter;
                            }else {
                                //TODO update DrawerItem
                                for(Long i: menuId_group.keySet()){
                                    Log.v("contains",i+ "=" +menuId_group.get(i));
                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //
                        }
                    });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String groupKey = dataSnapshot.getKey();
                for (long itemId: menuId_group.keySet()){
                    if(groupKey.matches(menuId_group.get(itemId))){
                        result.removeItem(itemId);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    isGroupDrawerInited = true;

    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                if (resultCode == RESULT_OK) {

                    handleSignInResponse(resultCode, data);
                    return;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }else if (requestCode == UPDATE_PROFILE  && resultCode  == RESULT_OK) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
                StorageReference photoRef = storageRef.child(mAuth.getCurrentUser().getUid()+".jpg");

                final IProfile profile = new ProfileDrawerItem().withName(mAuth.getCurrentUser().getDisplayName()).withEmail(mAuth.getCurrentUser().getEmail())
                        .withIcon(R.drawable.profile).withIdentifier(PROFILE_SETTING);
                headerResult.updateProfile(profile);
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    final long ONE_MEGABYTE = 1024*1024;
                    photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created

                            profile.withIcon(BitmapFactory.decodeFile(localFile.getPath()));
                            headerResult.updateProfile(profile);
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
                    Log.d("MainActivity",e.getMessage());
                }

            }


    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            Log.d("response",response.getEmail() + response.getIdpSecret());

            if(!result.isDrawerOpen())
                result.openDrawer();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar("sign in cancelled");
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar("no internet connection");
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar("unknown error");
                return;
            }
        }

        showSnackbar("unknown sign in response");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("MainActivity", "onConnectionFailed:" + connectionResult);
        ViewGroup container = (ViewGroup) findViewById(R.id.snackbar_layout);
        Snackbar.make(container, getString(R.string.google_play_services_error), Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackbar(String msg) {
        ViewGroup container = (ViewGroup) this.findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }
}
