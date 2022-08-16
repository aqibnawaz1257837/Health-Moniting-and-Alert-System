package com.sfcwarriors.healthalert.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.sfcwarriors.healthalert.Adapter.CaseHistoryAdapter;
import com.sfcwarriors.healthalert.Adapter.CaseHistoryModel;
import com.sfcwarriors.healthalert.LoginActivity;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.dashboard.EditeProfileActivty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import soup.neumorphism.NeumorphButton;

public class SettingFragment extends Fragment implements View.OnClickListener {

    RelativeLayout editPro,delete,supportbtn,signout,addCaseHist;
    ImageView editProfile;
    RecyclerView caseHistoryRec;
    View v;

    DatabaseReference myRef;
    FirebaseDatabase database;

    LottieDialog pdialog;
    ArrayList<CaseHistoryModel> caseHistoryModelArrayList=new ArrayList<>();
    ArrayList<String> keyList=new ArrayList<>();
    TextView sname,semail;
    ImageView sprodfile;
    FirebaseStorage storage;
    StorageReference storageReference;
    String uprofile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_setting, container, false);
        init();
        adpaterwork();
        return v;
    }

    public void getImage(){
        storageReference.child("images/"+ uprofile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(getContext()).load(uri).into(sprodfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
    private void init()
    {
        supportbtn=v.findViewById(R.id.support);
        signout=v.findViewById(R.id.sigout);
        addCaseHist=v.findViewById(R.id.addCasehis);
        editPro =v.findViewById(R.id.editPro);
        editProfile=v.findViewById(R.id.editProfile);

        delete=v.findViewById(R.id.deleteAcc);
        semail=v.findViewById(R.id.semail);
        sname=v.findViewById(R.id.sname);
        sprodfile=v.findViewById(R.id.sprofile);

        caseHistoryRec=v.findViewById(R.id.caseHistoryRec);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("caseHistoryData");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        editPro.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        supportbtn.setOnClickListener(this);
        signout.setOnClickListener(this);
        addCaseHist.setOnClickListener(this);
        delete.setOnClickListener(this);


        pdialog = new LottieDialog(getContext())
                .setAnimation(R.raw.loading)
                .setAnimationRepeatCount(LottieDrawable.INFINITE)
                .setAutoPlayAnimation(true)
                .setTitle("Please Wait")
                .setTitleColor(ContextCompat.getColor(getContext(),R.color.textColor))
                .setDialogHeightPercentage(.25f)
                .setMessage("Loading...")
                .setMessageColor(ContextCompat.getColor(getContext(),R.color.textColor))
                .setDialogBackground(Color.TRANSPARENT)

                .setCancelable(false);
        fetchDataFirBase();

        SharedPreferences sh = getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String uname = sh.getString("uname", "");
        uprofile = sh.getString("uprofile", "");
        String uemail = sh.getString("uemail", "");
        sname.setText(uname);
        semail.setText(uemail);
        getImage();
    }

    public void adpaterwork(){
        caseHistoryRec.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.support)
        {
            new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Support")
                    .setContentText(getContext().getString(R.string.supportmsg))
                    .setConfirmText("Acknowledge")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();

        }
        else if(view.getId()==R.id.deleteAcc)
        {
            new SweetAlertDialog(getContext(),SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Confirmation")
                    .setContentText("Are You Sure, You Want to Delete Account ?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
                    .setConfirmText("Delete!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        else if(view.getId()==R.id.addCasehis)
        {
            showCaseFormDialog();
        }
        else if(view.getId()==R.id.sigout)
        {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

            sharedPreferences.edit().clear().commit();

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();

        }
        else if(view.getId()==R.id.editPro || view.getId()==R.id.editProfile)
        {
            startActivity(new Intent(getContext(), EditeProfileActivty.class));
        }
    }

    private void showCaseFormDialog() {
        final Dialog dialog = new Dialog(getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.case_history_form);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView ddate=dialog.getWindow().findViewById(R.id.ddate);
        EditText dname=dialog.getWindow().findViewById(R.id.dName);
        EditText ddetail=dialog.getWindow().findViewById(R.id.dReason);
        NeumorphButton addCasehis=dialog.getWindow().findViewById(R.id.addCaseHisbtn);


        ddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDate(dialog.getContext(),ddate);
            }
        });


        addCasehis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dname.getText().toString().isEmpty())
                {
                    dname.setError("Enter a Diseases");
                    return;
                }
                if(ddetail.getText().toString().isEmpty())
                {
                    ddetail.setError("Enter Diseases Detail");
                    return;
                }
                if(ddate.getText().toString().contains("-"))
                {
                    ddate.setError("Enter a  Date");
                    return;
                }
                FirebaseAuth auth = FirebaseAuth.getInstance();

                CaseHistoryModel caseHistoryModel=new CaseHistoryModel(ddate.getText().toString(),ddetail.getText().toString(),dname.getText().toString(),auth.getUid());

                pdialog.show();
                myRef.push().setValue(caseHistoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pdialog.dismiss();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Errror",e.getMessage());
                        pdialog.dismiss();
                        dialog.dismiss();

                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sh = getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String uname = sh.getString("uname", "");
        String uemail = sh.getString("uemail", "");
         uprofile = sh.getString("uprofile", "");
        sname.setText(uname);
        semail.setText(uemail);
        getImage();
    }

    private void fetchDataFirBase() {


        pdialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.orderByChild("userID").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                caseHistoryModelArrayList.clear();
                keyList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());
                    keyList.add(snapshot.getKey().toString());

                    CaseHistoryModel imagemodel = snapshot.getValue(CaseHistoryModel.class);
                    caseHistoryModelArrayList.add(imagemodel);

                }
                Collections.reverse(caseHistoryModelArrayList);
                Collections.reverse(keyList);

                caseHistoryRec.setAdapter(new CaseHistoryAdapter(caseHistoryModelArrayList,keyList,getContext(),myRef));
                pdialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                pdialog.dismiss();

                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });
    }

    public  void showDate(Context context, TextView editText){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        editText.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}