package com.sfcwarriors.healthalert;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sfcwarriors.healthalert.Adapter.ScheduleFoldingCardAdopter;
import com.sfcwarriors.healthalert.dashboard.*;
import com.sfcwarriors.healthalert.fragment.AppoinmentFragment;
import com.sfcwarriors.healthalert.fragment.DashboardFragment;
import com.sfcwarriors.healthalert.fragment.FeedbackFragment;
import com.sfcwarriors.healthalert.fragment.HelpFragment;
import com.sfcwarriors.healthalert.fragment.ScheduleFragment;
import com.sfcwarriors.healthalert.fragment.SearchFragment;
import com.sfcwarriors.healthalert.fragment.SettingFragment;
import com.sfcwarriors.healthalert.fragment.VitalsFragment;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;
import com.sfcwarriors.healthalert.model.UserDataModel;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.Collections;

public class DashboardActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_DASHBOARD = 0;
    private static final int POS_REGISTRATION = 1;
    private static final int POS_APPOINMENT = 2;
    private static final int POS_FEEDBACK = 5;
    private static final int POS_HELP = 6;
    private static final int POS_LOGOUT = 7;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerView;



    //forDarkMode
    /*SwitchCompat switchCompat;
    SharedPreferences sf;*/
    LottieDialog dialog;
    TextView username,useremail;
    ImageView userprfoile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dlModeCondition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        /*switchCompat = findViewById(R.id.btn_switch);*/
        //lightnDarkMode();




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        init();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();




        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(0).setChecked(true),
                createItemFor(1),
                createItemFor(2),
                createItemFor(3),
                createItemFor(4),
                createItemFor(5),
                createItemFor(6),
                createItemFor(7)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(0);
    }

    public  void  init(){

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");
        username=slidingRootNav.getLayout().findViewById(R.id.userName);
        useremail=slidingRootNav.getLayout().findViewById(R.id.userEmail);
        userprfoile=slidingRootNav.getLayout().findViewById(R.id.userProfile);


        dialog = new LottieDialog(this)
                .setAnimation(R.raw.loading)
                .setAnimationRepeatCount(LottieDrawable.INFINITE)
                .setAutoPlayAnimation(true)
                .setTitle("Please Wait")
                .setTitleColor(ContextCompat.getColor(this,R.color.textColor))
                .setDialogHeightPercentage(.25f)
                .setMessage("Loading...")
                .setMessageColor(ContextCompat.getColor(this,R.color.textColor))
                .setDialogBackground(Color.TRANSPARENT)
                .setCancelable(false);

        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        String uname = sh.getString("uname", "");
        String uemail = sh.getString("uemail", "");
        if(uname.isEmpty())
        {
            fetchDataFirBase();
        }
        else{
            username.setText(uname);
            useremail.setText(uemail);
        }

    }
    @Override
    public void onItemSelected(int position) {

        switch (position){
            case 0:
                showFragment(new DashboardFragment());
                break;
            case 1:
                showFragment(new AppoinmentFragment());
                break;
            case 2:
                showFragment(new ScheduleFragment());
                break;
            case 3:
                showFragment(new VitalsFragment());
                break;
            case 4:
                showFragment(new SearchFragment());
                break;
            case 5:
                showFragment(new FeedbackFragment());
                break;
            case 6:
                showFragment(new HelpFragment());
                break;
            case 7:
                showFragment(new SettingFragment());
                break;
        }

        slidingRootNav.closeMenu();
        /*Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
        showFragment(selectedScreen);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDataFirBase();
        Log.e("sdsds","shahah");
    }



    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }


    public void fetchDataFirBase() {


        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.orderByChild("id").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);


                UserDataModel userDataModel;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());


                     userDataModel = snapshot.getValue(UserDataModel.class);

                     username.setText(userDataModel.getUname());
                     useremail.setText(userDataModel.getUemal());
                    getImage(userDataModel.getUserProfileIImage());
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);


                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("uname", userDataModel.getUname());
                    editor.putString("uemail", userDataModel.getUemal());
                    editor.putString("uaddress", userDataModel.getUaddress());
                    editor.putString("uphone", userDataModel.getUphone());
                    editor.putString("ugender", userDataModel.getGender());
                    editor.putString("uid", userDataModel.getId());
                    editor.putString("uprofile", userDataModel.getUserProfileIImage());
                    editor.putString("upassword", userDataModel.getUpassword());
                    editor.commit();
                }



                dialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


    public void getImage(String imageName){

        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        storageReference.child("images/"+ imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(getApplicationContext()).load(uri).into(userprfoile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }


    //forDarkModeFunctions
    /*public void dlModeCondition(){

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_Dark);
        }
        else {
            setTheme(R.style.Theme_Light);
        }
    }*/

    /*public void lightnDarkMode(){

        sf = getSharedPreferences("night",0);

        boolean bValue = sf.getBoolean("night_mode",true);
        if (bValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setChecked(true);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchCompat.setChecked(true);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchCompat.setChecked(false);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });
    }*/
}