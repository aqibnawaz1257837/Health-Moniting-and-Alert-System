package com.sfcwarriors.healthalert.Adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ramotion.foldingcell.FoldingCell;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleFoldingCardAdopter extends RecyclerView.Adapter<ScheduleFoldingCardAdopter.ViewHolder> {

    ArrayList<ScheduleDataModel> scheduleDataModelsList;
    ArrayList<String> keyList;
    DatabaseReference myRef;
    Context context;

    LottieDialog dialog;

    public ScheduleFoldingCardAdopter(ArrayList<ScheduleDataModel> scheduleDataModelsList, ArrayList<String> keyList, DatabaseReference myRef, Context context) {
        this.scheduleDataModelsList = scheduleDataModelsList;
        this.keyList = keyList;
        this.context = context;
        this.myRef = FirebaseDatabase.getInstance().getReference().child("dossageRecord");

        dialog = new LottieDialog(context)
                .setAnimation(R.raw.loading)
                .setAnimationRepeatCount(LottieDrawable.INFINITE)
                .setAutoPlayAnimation(true)
                .setTitle("Please Wait")
                .setTitleColor(ContextCompat.getColor(context,R.color.textColor))
                .setDialogHeightPercentage(.25f)
                .setMessage("Loading...")
                .setMessageColor(ContextCompat.getColor(context,R.color.textColor))
                .setDialogBackground(Color.TRANSPARENT)

                .setCancelable(false);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_folding_card,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleFoldingCardAdopter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.medName.setText(scheduleDataModelsList.get(position).getMedName());
        holder.sdate.setText(scheduleDataModelsList.get(position).getStartDate());
        holder.edate.setText(scheduleDataModelsList.get(position).getEndDate());
        holder.rtime.setText(scheduleDataModelsList.get(position).getNoRepeat());
        holder.gtime.setText(scheduleDataModelsList.get(position).getGapeTime());

        holder.medName1.setText(scheduleDataModelsList.get(position).getMedName());
        holder.medCat.setText(scheduleDataModelsList.get(position).getMedCatName());
        holder.sdate1.setText(scheduleDataModelsList.get(position).getStartDate());
        holder.edate1.setText(scheduleDataModelsList.get(position).getEndDate());
        holder.rtime1.setText(scheduleDataModelsList.get(position).getNoRepeat());
        holder.gtime1.setText(scheduleDataModelsList.get(position).getGapeTime());


        SharedPreferences sh = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        String uname = sh.getString("uname", "");


        String des="Dear "+uname+", you will Take  "+scheduleDataModelsList.get(position).getMedName()+"  "+scheduleDataModelsList.get(position).getMedCatName()+"" +
                " from "+scheduleDataModelsList.get(position).getStartDate()+" till "+scheduleDataModelsList.get(position).getEndDate()+" one the gape of "
                +scheduleDataModelsList.get(position).getGapeTime()+" hour and "+scheduleDataModelsList.get(position).getNoRepeat()+" time of a day " +
                " Please take care of Your Health so you can spend a good life";
        holder.desc.setText(des);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.fc.toggle(false);

            }
        });

        String key=keyList.get(position);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "Data Deleted", Toast.LENGTH_SHORT).show();
                myRef.child(key).removeValue();

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(key,scheduleDataModelsList.get(position));
            }
        });

    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
    private void showBottomSheetDialog(String key, ScheduleDataModel model) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
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

       TextView s_date = bottomSheetDialog.findViewById(R.id.sdate);
        TextView    e_date = bottomSheetDialog.findViewById(R.id.edate);
        Spinner madicineCatSpin = bottomSheetDialog.findViewById(R.id.madicineCatSpin);
        EditText medicineName = bottomSheetDialog.findViewById(R.id.medicineName);
        TextView sdate = bottomSheetDialog.findViewById(R.id.sdate);
        TextView edate = bottomSheetDialog.findViewById(R.id.edate);
        EditText rtime = bottomSheetDialog.findViewById(R.id.rtime);
        EditText gapetime = bottomSheetDialog.findViewById(R.id.gapetime);
        Button addSchedule = bottomSheetDialog.findViewById(R.id.addSchedule);

        addSchedule.setText("Update");

        madicineCatSpin.setSelection(getIndex(madicineCatSpin, model.getMedCatName()));
        medicineName.setText(model.getMedName());
        sdate.setText(model.getStartDate());
        edate.setText(model.getEndDate());
        rtime.setText(model.getNoRepeat());
        gapetime.setText(model.getGapeTime());


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
                myRef.child(key).setValue(scheduleDataModel).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                    }
                });

            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return scheduleDataModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medName,sdate,edate,rtime,gtime,medName1,medCat,sdate1,edate1,rtime1,gtime1,desc;
        FoldingCell fc;
        ImageView edit,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fc = (FoldingCell) itemView.findViewById(R.id.sche_folding_cell);

            //Tittle Fields | Header
            medName=itemView.findViewById(R.id.medName);
            sdate=itemView.findViewById(R.id.sdate);
            edate=itemView.findViewById(R.id.edate);
            rtime=itemView.findViewById(R.id.rtime);
            gtime=itemView.findViewById(R.id.gtime);

            //Content Fields | Footer | inner
            medName1=itemView.findViewById(R.id.medName1);
            medCat=itemView.findViewById(R.id.medCat);
            sdate1=itemView.findViewById(R.id.sdate1);
            edate1=itemView.findViewById(R.id.edate1);
            rtime1=itemView.findViewById(R.id.rtime1);
            gtime1=itemView.findViewById(R.id.gapetime1);

            //Edit and Delete Button
            edit=itemView.findViewById(R.id.editSch);
            delete=itemView.findViewById(R.id.deleteSch);
            desc=itemView.findViewById(R.id.sdec);

        }
    }
}
