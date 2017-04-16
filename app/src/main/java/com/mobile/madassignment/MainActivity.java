package com.mobile.madassignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mobile.madassignment.R;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private Map<Long, String> menuId_group;
    long id_counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuId_group = new HashMap<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        add_group_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,AddGroupActivity.class);
//
//                startActivity(intent);
//            }
//        });

        final IProfile profile = new ProfileDrawerItem().withName("Batman").withEmail("batman@gmail.com")
                .withIcon(R.drawable.profile).withIdentifier(105);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()

                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            int count = 100 + headerResult.getProfiles().size() + 1;
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count)
                                    .withEmail("batman" + count + "@gmail.com").withIcon(R.drawable.profile).withIdentifier(count);
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
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

                .addDrawerItems(

                ) // add the items we want to use with our Drawer

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
                            String group_key= menuId_group.get(id);
                            Log.v("starting fragment", "item id:"+id +" group_key:"+ group_key);
                          // Toast.makeText(MainActivity.this, id + group_key, Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("group_key", group_key);

                            MainFragment mainFragment = new MainFragment();
                            FragmentManager manager = getSupportFragmentManager();

                            mainFragment.setArguments(bundle);
                            manager.beginTransaction().replace(R.id.main_content,mainFragment).commit();
                        }
                    return false;

                    }
                })

                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
//                .withShowDrawerUntilDraggedOpened(true)
                .build();


        if(!result.isDrawerOpen()){
            result.openDrawer();
        }
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
        }else{
            Toast.makeText(this, "connected to Network",Toast.LENGTH_SHORT ).show();
        }
        //get data from firebase
        DatabaseReference groupRef = mRootRef.child("groups");

        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ++id_counter;

                result.addItem(
                        new PrimaryDrawerItem().withName(dataSnapshot.child("name").getValue().toString())
                        .withDescription("mun_members")
                        .withIcon(R.drawable.profile)
                        .withIdentifier(id_counter)
                        .withSelectable(true)
                        .withBadgeStyle(new BadgeStyle()
                                .withTextColor(Color.WHITE)
                                .withColorRes(R.color.md_red_700))
                );
                Log.v("fire_data", dataSnapshot.getKey());

                menuId_group.put(id_counter,dataSnapshot.getKey());
                Log.v("map_id_data",id_counter+"..."+menuId_group.get(id_counter));


//                if(id_counter==1){
//                    String group_key= menuId_group.get(id_counter);
//                    Log.v("default fragment", "item id:"+1 +" group_key:"+ group_key);
//                    // Toast.makeText(MainActivity.this, id + group_key, Toast.LENGTH_SHORT).show();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("group_key", group_key);
//
//                    MainFragment mainFragment = new MainFragment();
//                    FragmentManager manager = getSupportFragmentManager();
//
//                    mainFragment.setArguments(bundle);
//                    manager.beginTransaction().replace(R.id.main_content,mainFragment).commit();
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String removedGroupKey= dataSnapshot.getKey();
                for(long id: menuId_group.keySet()){
                    if(menuId_group.get(id) == removedGroupKey){
                        //   menu.removeItem(id);
                        Log.v("groupremoved",removedGroupKey+"->"+id);
                        break;
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
}
