package com.sfcwarriors.healthalert.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sfcwarriors.healthalert.Adapter.AppointmentFoldingCardAdapter;
import com.sfcwarriors.healthalert.Adapter.ScheduleCardRecAdapter;
import com.sfcwarriors.healthalert.Adapter.ScheduleFoldingCardAdopter;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    TextView s_date, e_date;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerView;
    View v;

    ArrayList<ScheduleDataModel> scheduleDataModelsList;
    ArrayList<String> keyList;

    LottieDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_schedule, container, false);


        //Schedule Date Model List over here
        scheduleDataModelsList=new ArrayList<>();
        keyList=new ArrayList<>();
        init();
        fetchDataFirBase();

        //Floating Action Button Work
        FloatingActionButton floatingActionButton=v.findViewById(R.id.showBottom);
        floatingActionButton.setOnClickListener(this);

        /*RecyclerView sche_res_view = v.findViewById(R.id.scheduleFoldingCard);
        sche_res_view.setLayoutManager(new LinearLayoutManager(getContext()));
        sche_res_view.setAdapter(new ScheduleFoldingCardAdopter());*/

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void fetchDataFirBase() {


        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.orderByChild("userID").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                scheduleDataModelsList.clear();
                keyList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());
                    keyList.add(snapshot.getKey().toString());

                    ScheduleDataModel imagemodel = snapshot.getValue(ScheduleDataModel.class);
                    scheduleDataModelsList.add(imagemodel);

                }
                Collections.reverse(scheduleDataModelsList);
                Collections.reverse(keyList);

                recyclerView.setAdapter(new ScheduleFoldingCardAdopter(scheduleDataModelsList, keyList, myRef, getContext()));
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                dialog.dismiss();

                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });
    }

    private void init() {

        recyclerView=v.findViewById(R.id.scheduleFoldingCard);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("dossageRecord");

        dialog = new LottieDialog(getContext())
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

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.showBottom)
        {
            showBottomSheetDialog();
        }
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.schedule_bottom_sheet_form);

        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);


        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        s_date = bottomSheetDialog.findViewById(R.id.sdate);
        e_date = bottomSheetDialog.findViewById(R.id.edate);
        Spinner madicineCatSpin = bottomSheetDialog.findViewById(R.id.madicineCatSpin);
        EditText medicineName = bottomSheetDialog.findViewById(R.id.medicineName);
        TextView sdate = bottomSheetDialog.findViewById(R.id.sdate);
        TextView edate = bottomSheetDialog.findViewById(R.id.edate);
        EditText rtime = bottomSheetDialog.findViewById(R.id.rtime);
        EditText gapetime = bottomSheetDialog.findViewById(R.id.gapetime);
        Button addSchedule = bottomSheetDialog.findViewById(R.id.addSchedule);

        ProgressDialog progressDialog=new ProgressDialog(bottomSheetDialog.getContext());

        progressDialog.setTitle("Data Inserting");
        progressDialog.setMessage("wait...");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        s_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(bottomSheetDialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                s_date.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        e_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(bottomSheetDialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                e_date.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(medicineName.getText().toString().isEmpty())
                {
                    medicineName.setError("Enter a Medicine Name First");
                    return;
                }

                if(rtime.getText().toString().isEmpty())
                {
                    rtime.setError("Enter a Repetition");
                    return;
                }

                if(gapetime.getText().toString().isEmpty())
                {
                    gapetime.setError("Enter a Gape Time");
                    return;
                }
                if(sdate.getText().toString().contains("-"))
                {
                    sdate.setError("Enter a Start Date");
                    return;
                }
                if(edate.getText().toString().contains("-"))
                {
                    edate.setError("Enter a End Date");
                    return;
                }

                dialog.show();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                ScheduleDataModel scheduleDataModel=new ScheduleDataModel(madicineCatSpin.getSelectedItem().toString(),
                        medicineName.getText().toString(),
                        sdate.getText().toString(),edate.getText().toString(),
                        rtime.getText().toString(),gapetime.getText().toString(),auth.getUid());
                myRef.push().setValue(scheduleDataModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Errror",e.getMessage());
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();

                    }
                });

            }
        });

        bottomSheetDialog.show();
    }
}