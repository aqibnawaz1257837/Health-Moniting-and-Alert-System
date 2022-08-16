package com.sfcwarriors.healthalert.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.sfcwarriors.healthalert.Adapter.VitalsFoldingCardAdopter;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.VitalModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import soup.neumorphism.NeumorphButton;

public class VitalsFragment extends Fragment implements View.OnClickListener {

    View v;
    FirebaseDatabase database;
    DatabaseReference myRef1;
    LottieDialog dialog;
    ArrayList<VitalModel> vitalModelArrayList=new ArrayList<>();
    ArrayList<String> keyList=new ArrayList<>();
    RecyclerView vitals_res_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v= inflater.inflate(R.layout.fragment_vitals, container, false);

        FloatingActionButton floatingActionButton=v.findViewById(R.id.showBottom);
        floatingActionButton.setOnClickListener(this);

        init();



        return  v;

    }

    private void init() {

        database = FirebaseDatabase.getInstance();
        myRef1 = database.getReference().child("vitalsRecord");
        vitals_res_view = v.findViewById(R.id.vitalsFoldingCard);
        vitals_res_view.setLayoutManager(new LinearLayoutManager(getContext()));
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
        fetchDataFirBase();

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.showBottom)
        {
            showBottomSheetDialog();
        }
    }

    public int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }
    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.vitals_bottom_sheet_form);


        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);


        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        TextView date = bottomSheetDialog.findViewById(R.id.date);
        TextView time = bottomSheetDialog.findViewById(R.id.time);
        Spinner catSpin = bottomSheetDialog.findViewById(R.id.catSpin);
        EditText vitalValue = bottomSheetDialog.findViewById(R.id.vitalValue);
        NeumorphButton addVitals = bottomSheetDialog.findViewById(R.id.newwaddVitals);


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog  datePickerDialog = new DatePickerDialog(bottomSheetDialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                date.setText(day + "/" + (month + 1) + "/" + year);
                                Log.e("Asas",String.valueOf(datePicker.getMinDate()));
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(bottomSheetDialog.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        addVitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(vitalValue.getText().toString().isEmpty())
                {
                    vitalValue.setError("Enter a Value First");
                    return;
                }
                if(date.getText().toString().contains("-"))
                {
                    date.setError("Enter a  Date");
                    return;
                }
                if(time.getText().toString().contains("time"))
                {
                    time.setError("Enter a Time ");
                    return;
                }

//                dialog.show();
                FirebaseAuth auth = FirebaseAuth.getInstance();



                VitalModel vitalModel=new VitalModel("catSpin.getSelectedItem().toString()",date.getText().toString(),
                        time.getText().toString(),vitalValue.getText().toString(),auth.getUid(),day);


                myRef1.push().setValue(vitalModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Vital Inserted", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                       bottomSheetDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Errror",e.getMessage());
                        Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();

//                        dialog.dismiss();

                    }
                });
            }
        });
        bottomSheetDialog.show();
    }

    private void fetchDataFirBase() {


        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef1.orderByChild("userID").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                vitalModelArrayList.clear();
                keyList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());
                    keyList.add(snapshot.getKey().toString());

                    VitalModel imagemodel = snapshot.getValue(VitalModel.class);
                    vitalModelArrayList.add(imagemodel);

                }
                Collections.reverse(vitalModelArrayList);
                Collections.reverse(keyList);

                vitals_res_view.setAdapter(new VitalsFoldingCardAdopter(vitalModelArrayList,keyList,getContext(), myRef1));
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

}