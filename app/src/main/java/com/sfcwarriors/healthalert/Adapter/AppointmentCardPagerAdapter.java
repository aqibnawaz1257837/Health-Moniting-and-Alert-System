package com.sfcwarriors.healthalert.Adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ApointmentModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import soup.neumorphism.NeumorphButton;

public class AppointmentCardPagerAdapter extends PagerAdapter {

    private Context Mcontext;

    ArrayList<ApointmentModel> apointmentModelArrayList;
    ArrayList<String> keyList;
    DatabaseReference myRef;

    public AppointmentCardPagerAdapter(Context mcontext, ArrayList<ApointmentModel> apointmentModelArrayList, ArrayList<String> keyList, DatabaseReference myRef) {
        Mcontext = mcontext;
        this.apointmentModelArrayList = apointmentModelArrayList;
        this.keyList = keyList;
        this.myRef = myRef;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.visit_card,null);

//        ImageView featured_image = sliderLayout.findViewById(R.id.my_featured_image);
        TextView drname = sliderLayout.findViewById(R.id.vdrname);
        TextView vdieses = sliderLayout.findViewById(R.id.vdease);
        TextView vdate = sliderLayout.findViewById(R.id.vdate);
        TextView vtime = sliderLayout.findViewById(R.id.vtime);
        MaterialButton vdel = sliderLayout.findViewById(R.id.vdel);
        MaterialButton vedit = sliderLayout.findViewById(R.id.vedit);


        drname.setText(apointmentModelArrayList.get(position).getDoctName());
        vdieses.setText(apointmentModelArrayList.get(position).getPurpose());
        vdate.setText(apointmentModelArrayList.get(position).getDate());
        vtime.setText(apointmentModelArrayList.get(position).getTime());

        int ss=position;
        vdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(Mcontext, " ,mm,n", Toast.LENGTH_SHORT).show();
//
                Toast.makeText(Mcontext, " Data Deletedd", Toast.LENGTH_LONG).show();

                myRef.child(keyList.get(position)).removeValue();
            }
        });

        vedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(keyList.get(position), apointmentModelArrayList.get(position));
            }
        });
//
//        featured_image.setImageResource(theSlideItemsModelClassList.get(position).getFeatured_image());
//        caption_title.setText(theSlideItemsModelClassList.get(position).getThe_caption_Title());
        container.addView(sliderLayout);
        return sliderLayout;
    }
    private void showBottomSheetDialog(String key, ApointmentModel model) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Mcontext);
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

    @Override
    public float getPageWidth(final int position) {
        return 0.9f;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return apointmentModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

}