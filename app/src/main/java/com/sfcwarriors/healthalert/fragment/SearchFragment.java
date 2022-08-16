package com.sfcwarriors.healthalert.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sfcwarriors.healthalert.Adapter.AppointmentCardPagerAdapter;
import com.sfcwarriors.healthalert.Adapter.AppointmentFoldingCardAdapter;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ApointmentModel;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;
import com.sfcwarriors.healthalert.model.VitalModel;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import soup.neumorphism.NeumorphButton;

public class SearchFragment extends Fragment {

    NeumorphButton searchbtn;
    EditText searchedit;

    Spinner cat;

    View v;
    LottieDialog dialog;
    ArrayList<ApointmentModel> apointmentModelArrayList=new ArrayList<>();
    ApointmentModel apointmentModel;
    ScheduleDataModel scheduleDataModel;
    VitalModel vitalModel;
    TextView searchtile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_search, container, false);

         searchbtn=v.findViewById(R.id.searchBtn);
        searchedit=v.findViewById(R.id.search);
        cat=v.findViewById(R.id.category);
        searchtile=v.findViewById(R.id.searchtile);


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
        searchtile.setText("Search with Doctor Name");

        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    searchtile.setText("Search with Doctor Name");
                }
                else if(i==1)
                {
                    searchtile.setText("Search with Medicine Name");
                }
                else if(i==2){

                    searchtile.setText("Search with date (hint:23/4/2021)");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(searchedit.getText().toString().isEmpty())
                {
                    searchedit.setText("Fill First");
                    return;
                }

                String category=cat.getSelectedItem().toString();
                String searchKey=searchedit.getText().toString();
                dialog.show();


                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

                if(category.equals("Appoinntments"))
                {
                    DatabaseReference databaseReference=firebaseDatabase.getReference().child("appointmentsRecord");

                    databaseReference.orderByChild("doctName").equalTo(searchKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            apointmentModelArrayList.clear();

                            if(dataSnapshot.exists())
                            {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                    Log.e("dfdfdf",snapshot.getKey().toString());

                                    apointmentModel = snapshot.getValue(ApointmentModel.class);
                                }


                                new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Doctor Name "+apointmentModel.getDoctName())
                                        .setContentText("Purpose "+apointmentModel.getPurpose())
                                        .setConfirmText("Date :"+apointmentModel.getDate()+" time :"+apointmentModel.getTime())
                                        .show();
                                dialog.dismiss();
                            }
                            dialog.dismiss();
                            searchedit.setText("");
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("not found",error.getMessage());

                            dialog.dismiss();
                            searchedit.setText("");
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else   if(category.equals("Schedules"))
                {


                    DatabaseReference databaseReference=firebaseDatabase.getReference().child("dossageRecord");


                    databaseReference.orderByChild("medName").equalTo(searchKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            apointmentModelArrayList.clear();

                            if(dataSnapshot.exists())
                            {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                    Log.e("dfdfdf",snapshot.getKey().toString());

                                    scheduleDataModel = snapshot.getValue(ScheduleDataModel.class);
                                }


                                new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Medicine Name "+scheduleDataModel.getMedName()+" Category "+scheduleDataModel.getMedCatName())
                                        .setContentText("Repeat Time "+scheduleDataModel.getNoRepeat()+" Gape Time "+scheduleDataModel.getGapeTime()+"From Date :"+scheduleDataModel.getStartDate())
                                        .setConfirmText(" Till :"+scheduleDataModel.getEndDate())
                                        .show();
                                dialog.dismiss();
                            }
                            dialog.dismiss();
                            searchedit.setText("");
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("not found",error.getMessage());

                            dialog.dismiss();
                            searchedit.setText("");
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });




                }
                else  if(category.equals("Health Vitals"))
                {
                    DatabaseReference databaseReference=firebaseDatabase.getReference().child("vitalsRecord");

                    databaseReference.orderByChild("date").equalTo(searchKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                    Log.e("dfdfdf",snapshot.getKey().toString());

                                    vitalModel = snapshot.getValue(VitalModel.class);
                                }


                                new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Your "+vitalModel.getCat()+" on  "+vitalModel.getDate())
                                        .setContentText(vitalModel.getValue())
                                        .show();
                                dialog.dismiss();
                            }
                            dialog.dismiss();
                            searchedit.setText("");
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("not found",error.getMessage());

                            dialog.dismiss();
                            searchedit.setText("");
                            Toast.makeText(getContext(), "Data Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });

                }





            }
        });



        return v;
    }
}