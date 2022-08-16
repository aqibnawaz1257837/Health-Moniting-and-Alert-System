package com.sfcwarriors.healthalert.Adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sfcwarriors.healthalert.model.ApointmentModel;
import com.sfcwarriors.healthalert.model.VitalModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import soup.neumorphism.NeumorphButton;

public class AppointmentFoldingCardAdapter extends RecyclerView.Adapter<AppointmentFoldingCardAdapter.ViewHolder> {

    ArrayList<ApointmentModel> apointmentModelArrayList;
    ArrayList<String> keyList;
    Context context;
    DatabaseReference myRef;


    public AppointmentFoldingCardAdapter(ArrayList<ApointmentModel> apointmentModelArrayList, ArrayList<String> keyList, Context context, DatabaseReference myRef) {
        this.apointmentModelArrayList = apointmentModelArrayList;
        this.keyList = keyList;
        this.context = context;
        this.myRef = myRef;
//        dialog = new LottieDialog(context)
//                .setAnimation(R.raw.loading)
//                .setAnimationRepeatCount(LottieDrawable.INFINITE)
//                .setAutoPlayAnimation(true)
//                .setTitle("Please Wait")
//                .setTitleColor(ContextCompat.getColor(context,R.color.textColor))
//                .setDialogHeightPercentage(.25f)
//                .setMessage("Loading...")
//                .setMessageColor(ContextCompat.getColor(context,R.color.textColor))
//                .setDialogBackground(Color.TRANSPARENT)
//
//                .setCancelable(false);

    }

    @NonNull
    @Override
    public AppointmentFoldingCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_folding_card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentFoldingCardAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        String drname=apointmentModelArrayList.get(position).getDoctName();
        String date=apointmentModelArrayList.get(position).getDate();
        String time=apointmentModelArrayList.get(position).getTime();
        String purpose=apointmentModelArrayList.get(position).getPurpose();

        holder.docname.setText(drname);
        holder.date.setText(date);
        holder.time.setText(time);
        holder.purpose.setText(purpose);


        holder.docname1.setText(drname);
        holder.date1.setText(" Appointment Date: "+date);
        holder.time1.setText( " Appointment Time: "+time);
        holder.purpose1.setText(purpose);


        SharedPreferences sh = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        String uname = sh.getString("uname", "");


        String desc="Dear "+uname+" you appointment for "+drname+" on the date of "+date+" and time is "+time+ " Please take care of Your Health so you can spend a good life";

        holder.desc.setText(desc);
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
                showBottomSheetDialog( key, apointmentModelArrayList.get(position));
            }
        });
        // attach click listener to folding cell

    }

    @Override
    public int getItemCount() {
        return apointmentModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         FoldingCell fc;
         TextView docname,date,time,purpose,desc;
         TextView docname1,date1,time1,purpose1;
         ImageView edit,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fc   = (FoldingCell) itemView.findViewById(R.id.app_folding_cell);

            docname=itemView.findViewById(R.id.adocname);
            date=itemView.findViewById(R.id.adate);
            time=itemView.findViewById(R.id.atime);
            purpose=itemView.findViewById(R.id.apurpose);

            docname1=itemView.findViewById(R.id.adocname1);
            date1=itemView.findViewById(R.id.adate1);
            time1=itemView.findViewById(R.id.atime1);
            purpose1=itemView.findViewById(R.id.apurpose1);

            desc=itemView.findViewById(R.id.adesc);

            edit=itemView.findViewById(R.id.editeAppoin);
            delete=itemView.findViewById(R.id.deleteAppoin);


        }
    }

    private void showBottomSheetDialog(String key, ApointmentModel model) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.appointment_bottom_sheet_form);

        TextView date = bottomSheetDialog.findViewById(R.id.date);
        TextView time = bottomSheetDialog.findViewById(R.id.time);
        EditText doctorName=bottomSheetDialog.findViewById(R.id.doctorName);
        EditText purpose=bottomSheetDialog.findViewById(R.id.purpose);
        NeumorphButton addAppointment=bottomSheetDialog.findViewById(R.id.addAppointment);


        addAppointment.setText("Update");
        date.setText(model.getDate());
        time.setText(model.getTime());
        purpose.setText(model.getPurpose());
        doctorName.setText(model.getDoctName());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);


        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);


        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

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

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(doctorName.getText().toString().isEmpty())
                {
                    doctorName.setError("Enter a Value First");
                    return;
                }
                if(purpose.getText().toString().isEmpty())
                {
                    purpose.setError("Enter a Value First");
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

                ApointmentModel apointmentModel=new ApointmentModel(doctorName.getText().toString(),purpose.getText().toString(),date.getText().toString(),time.getText().toString(),auth.getUid());


                myRef.child(key).setValue(apointmentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
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


//        LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
//        LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
//        LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

        bottomSheetDialog.show();
    }

}