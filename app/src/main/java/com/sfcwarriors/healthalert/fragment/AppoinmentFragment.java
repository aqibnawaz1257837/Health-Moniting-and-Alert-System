package com.sfcwarriors.healthalert.fragment;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.sfcwarriors.healthalert.Adapter.VitalsFoldingCardAdopter;
import com.sfcwarriors.healthalert.AlarmData.broadcastReceiver.AlarmBroadcastReceiver;

import com.sfcwarriors.healthalert.MyBroadcastReceiver;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ApointmentModel;
import com.sfcwarriors.healthalert.model.VitalModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import soup.neumorphism.NeumorphButton;


public class AppoinmentFragment extends Fragment implements View.OnClickListener {

    String adate;
    String atime;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView app_res_view;
    View v;
    LottieAnimationView app_button;
    LottieDialog dialog;

    ArrayList<ApointmentModel> apointmentModelArrayList=new ArrayList<>();
    ArrayList<String> keyList=new ArrayList<>();
    AlarmManager alarmManager;
    public static int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_appoinment, container, false);

        /*FloatingActionButton floatingActionButton=v.findViewById(R.id.showBottom);
        floatingActionButton.setOnClickListener(this);*/
        init();
        app_button.setOnClickListener(this);




        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        //startAlert();
        adate="15-08-2022";
        atime="03:23";

        app_res_view = v.findViewById(R.id.appointmentFoldingCard);
        app_button = v.findViewById(R.id.showBottom);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("appointmentsRecord");
        app_res_view.setLayoutManager(new LinearLayoutManager(getContext()));

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

    private void fetchDataFirBase() {


        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.orderByChild("userID").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                apointmentModelArrayList.clear();
                keyList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());
                    keyList.add(snapshot.getKey().toString());

                    ApointmentModel imagemodel = snapshot.getValue(ApointmentModel.class);
                    apointmentModelArrayList.add(imagemodel);

                }
                Collections.reverse(apointmentModelArrayList);
                Collections.reverse(keyList);

                app_res_view.setAdapter(new AppointmentFoldingCardAdapter(apointmentModelArrayList,keyList,getContext(),myRef));
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



    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.appointment_bottom_sheet_form);

        TextView date = bottomSheetDialog.findViewById(R.id.date);
        TextView time = bottomSheetDialog.findViewById(R.id.time);
        EditText doctorName=bottomSheetDialog.findViewById(R.id.doctorName);
        EditText purpose=bottomSheetDialog.findViewById(R.id.purpose);
        NeumorphButton addAppointment=bottomSheetDialog.findViewById(R.id.addAppointment);

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

                                adate=day + "-" + (month + 1) + "-" + year;
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
                        atime=selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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

                dialog.show();
                myRef.push().setValue(apointmentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                        createAnAlarm(adate,atime);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Data not Inserted", Toast.LENGTH_SHORT).show();

                        Log.e("Errror",e.getMessage());
                        dialog.dismiss();

                    }
                });

                alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);


            }
        });


//        LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
//        LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
//        LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

        bottomSheetDialog.show();
    }


//    private void createTask(String date,String time) {
//        class saveTaskInBackend extends AsyncTask<Void, Void, Void> {
//            @SuppressLint("WrongThread")
//            @Override
//            protected Void doInBackground(Void... voids) {
//                Task createTask = new Task();
//                createTask.setTaskTitle("title");
//                createTask.setTaskDescrption("description");
//                createTask.setDate(date);
//                createTask.setLastAlarm(time);
//                createTask.setEvent("event");
//
////                if (!isEdit)
////                    DatabaseClient.getInstance(getActivity()).getAppDatabase()
////                            .dataBaseAction()
////                            .insertDataIntoTaskList(createTask);
////                else
//                    DatabaseClient.getInstance(getActivity()).getAppDatabase()
//                            .dataBaseAction()
//                            .updateAnExistingRow(0, "task",
//                                    "desc",
//                                    date,
//                                    time,
//                                    "");
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    createAnAlarm(date,time);
//                }
//            }
//        }
//        saveTaskInBackend st = new saveTaskInBackend();
//        st.execute();
//    }

    public void startAlert(){
        int i = 10;
        Intent intent = new Intent(getContext(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext().getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 1000), pendingIntent);
        Toast.makeText(getContext(), "Alarm set in " + i + " seconds",Toast.LENGTH_LONG).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm(String date,String time) {

            String[] items1 = date.toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = time.toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

            Calendar cur_cal = new GregorianCalendar();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            cal.set(Calendar.MINUTE, Integer.parseInt(min));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd));

        Intent intent = new Intent(getContext(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext().getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,  (cal.getTimeInMillis() ), pendingIntent);


//        Toast.makeText(getContext(), "Alarm set in " + (cal.getTimeInMillis()/1000) + " seconds",Toast.LENGTH_LONG).show();
//        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
//
//        Intent alarmIntent = new Intent(getContext(), AlarmBroadcastReceiver.class);
//            alarmIntent.putExtra("TITLE","title");
//            alarmIntent.putExtra("DESC","desc");
//            alarmIntent.putExtra("DATE", date);
//            alarmIntent.putExtra("TIME",time);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                } else {
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                }
//                count ++;
//
//                PendingIntent intent = PendingIntent.getBroadcast(getContext(), count, alarmIntent, 0);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
//                    } else {
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
//                    }
//                }
//                count ++;
//            }

    }

//    private void showTaskFromId() {
//
//        class showTaskFromId extends AsyncTask<Void, Void, Void> {
//            @SuppressLint("WrongThread")
//            @Override
//            protected Void doInBackground(Void... voids) {
//                task = DatabaseClient.getInstance(getActivity()).getAppDatabase()
//                        .dataBaseAction().selectDataFromAnId(taskId);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                setDataInUI();
//            }
//        }
//        showTaskFromId st = new showTaskFromId();
//        st.execute();
//    }

}