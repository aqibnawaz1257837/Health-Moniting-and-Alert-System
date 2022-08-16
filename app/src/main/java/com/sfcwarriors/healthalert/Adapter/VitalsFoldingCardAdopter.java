package com.sfcwarriors.healthalert.Adapter;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.ramotion.foldingcell.FoldingCell;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;
import com.sfcwarriors.healthalert.model.VitalModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import soup.neumorphism.NeumorphButton;

public class VitalsFoldingCardAdopter extends RecyclerView.Adapter<VitalsFoldingCardAdopter.MyViewHolder> {

    ArrayList<VitalModel> vitalModelArrayList;
    ArrayList<String> keyList;
    Context context;
    DatabaseReference myRef;


    public VitalsFoldingCardAdopter(ArrayList<VitalModel> vitalModelArrayList, ArrayList<String> keyList, Context context, DatabaseReference myRef) {
        this.vitalModelArrayList = vitalModelArrayList;
        this.keyList = keyList;
        this.context = context;
        this.myRef = myRef;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.vitals_folding_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {



        holder.vcat.setText(vitalModelArrayList.get(position).getCat());
        holder.vvalue.setText(vitalModelArrayList.get(position).getValue());
        holder.vdate.setText(vitalModelArrayList.get(position).getDate());
        holder.vtime.setText(vitalModelArrayList.get(position).getTime());
        holder.vcat1.setText(vitalModelArrayList.get(position).getCat());
        holder.vvalue1.setText(vitalModelArrayList.get(position).getValue());
        holder.vdate1.setText(vitalModelArrayList.get(position).getDate());
        holder.vtime1.setText(vitalModelArrayList.get(position).getTime());

        SharedPreferences sh = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        String uname = sh.getString("uname", "");


        String des="Dear "+uname+", your "+vitalModelArrayList.get(position).getCat()+" is "+vitalModelArrayList.get(position).getValue()+"" +
                " on this date "+vitalModelArrayList.get(position).getDate()+" and time "+vitalModelArrayList.get(position).getTime()+" " +
                " Please take care of Your Health so you can spend a good life";
        holder.vdesc.setText(des);

        //  holder.vdesc.setText(vitalModelArrayList.get(position).getCat());


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
                showBottomSheetDialog( key, vitalModelArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return vitalModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        FoldingCell fc;

        TextView vcat,vvalue,vdate,vtime;
        TextView vcat1,vvalue1,vdate1,vtime1,vdesc;
        ImageView edit,delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fc   = (FoldingCell) itemView.findViewById(R.id.vitals_folding_cell);
            vcat   =  itemView.findViewById(R.id.vcat);
            vvalue   =  itemView.findViewById(R.id.vvalue);
            vdate   =  itemView.findViewById(R.id.vdate);
            vtime   =  itemView.findViewById(R.id.vtime);
            vcat1   =  itemView.findViewById(R.id.vcat1);
            vvalue1   =  itemView.findViewById(R.id.vvalue1);
            vdate1   =  itemView.findViewById(R.id.vdate1);
            vtime1   =  itemView.findViewById(R.id.vtime1);
            vdesc   =  itemView.findViewById(R.id.vdesc);
            edit   =  itemView.findViewById(R.id.editeVital);
            delete   =  itemView.findViewById(R.id.deletevital);

        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
    private void showBottomSheetDialog(String key, VitalModel model) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
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

        addVitals.setText("Update");

        date.setText(model.getDate());
        time.setText(model.getTime());
        vitalValue.setText(model.getValue());
        catSpin.setSelection(getIndex(catSpin, model.getCat()));


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(bottomSheetDialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                date.setText(day + "/" + (month + 1) + "/" + year);
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


                FirebaseAuth auth = FirebaseAuth.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);


                VitalModel vitalModel=new VitalModel(catSpin.getSelectedItem().toString(),date.getText().toString(),
                        time.getText().toString(),vitalValue.getText().toString(),auth.getUid(),day);


                myRef.child(key).setValue(vitalModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                        bottomSheetDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Errror",e.getMessage());


                    }
                });
            }
        });
        bottomSheetDialog.show();
    }

}