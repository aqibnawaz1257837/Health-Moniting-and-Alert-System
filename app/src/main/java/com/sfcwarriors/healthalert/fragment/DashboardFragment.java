package com.sfcwarriors.healthalert.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sfcwarriors.healthalert.Adapter.AppointmentFoldingCardAdapter;
import com.sfcwarriors.healthalert.Adapter.ScheduleCardRecAdapter;
import com.sfcwarriors.healthalert.Adapter.AppointmentCardPagerAdapter;
import com.sfcwarriors.healthalert.Adapter.ScheduleFoldingCardAdopter;
import com.sfcwarriors.healthalert.Adapter.VitalsFoldingCardAdopter;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ApointmentModel;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;
import com.sfcwarriors.healthalert.model.VitalModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class DashboardFragment extends Fragment {


    TextView mtxt;
    View view;
    ViewPager appointmentViewPager;

    RecyclerView scheduleRecycler;

    AppointmentCardPagerAdapter visitViewPagerAdapter;
    ScheduleCardRecAdapter scheduleCardAdopter;

    private static final int MAX_X_VALUE = 7;
    private static final int MAX_Y_VALUE = 300;
    private static final int MIN_Y_VALUE = 5;
    private static final int GROUPS = 3;
    private static final String GROUP_1_LABEL = "Blood Pressure";
    private static final String GROUP_2_LABEL = "Glucose";
    private static final String GROUP_3_LABEL = "Weight";
    private static final float BAR_SPACE = 0.05f;
    private static final float BAR_WIDTH = 0.2f;
    private BarChart chart;
    boolean isClicked=false;
    LottieDialog dialog;

    ArrayList<ApointmentModel> apointmentModelArrayList=new ArrayList<>();
    ArrayList<String> appkeylisyt=new ArrayList<>();


    ArrayList<ScheduleDataModel> scheduleDataModelsList=new ArrayList<>();
    ArrayList<String> schkelist=new ArrayList<>();

    ArrayList<VitalModel> vitalModelArrayList=new ArrayList<>();
    ArrayList<String> vitalkeyl=new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference myRef;

    LottieAnimationView schdLotie,apdLotie;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_dashboard, container, false);

        chart = view.findViewById(R.id.fragment_groupedbarchart_chart);

        init();

        return view;
    }

    private void init() {
// Initializing the ViewPager Object
        appointmentViewPager =view.findViewById(R.id.viewPagerMain);
        appointmentViewPager.setPageMargin(12);
        // Initializing the ScheduleViewPager Object
        scheduleRecycler  =view.findViewById(R.id.schedulerCard);
        schdLotie  =view.findViewById(R.id.schdLotie);
        apdLotie  =view.findViewById(R.id.apdLotie);

        scheduleRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


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

        fetchAppointment();
        fetchScheduleDataFirBase();
        fetchVitalDataFirBase();
    }

    private void fetchScheduleDataFirBase() {

        database = FirebaseDatabase.getInstance();
       DatabaseReference myRefSch = database.getReference().child("dossageRecord");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        myRefSch.orderByChild("userID").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                if(scheduleDataModelsList.size()>0 && schkelist.size()>0)
                {
                    scheduleDataModelsList.clear();
                    schkelist.clear();

                }


                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());
                    schkelist.add(snapshot.getKey().toString());

                    ScheduleDataModel imagemodel = snapshot.getValue(ScheduleDataModel.class);
                    scheduleDataModelsList.add(imagemodel);

                }
                if(scheduleDataModelsList.size()>0)
                {
                    schdLotie.setVisibility(View.GONE);
                }


                Collections.reverse(scheduleDataModelsList);
                Collections.reverse(schkelist);

                // Initializing the ScheduleViewPager
                scheduleCardAdopter = new ScheduleCardRecAdapter(scheduleDataModelsList, schkelist, myRefSch, getContext());

                scheduleRecycler.setAdapter(scheduleCardAdopter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });

        if(scheduleDataModelsList.isEmpty())
        {
            schdLotie.setVisibility(View.VISIBLE);

        }
    }

    private void fetchAppointment() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference   myRefappoint = database.getReference().child("appointmentsRecord");


        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRefappoint.orderByChild("userID").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                apointmentModelArrayList.clear();
                appkeylisyt.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());
                    appkeylisyt.add(snapshot.getKey().toString());

                    ApointmentModel imagemodel = snapshot.getValue(ApointmentModel.class);
                    apointmentModelArrayList.add(imagemodel);

                }
                Collections.reverse(apointmentModelArrayList);
                Collections.reverse(appkeylisyt);
                if(apointmentModelArrayList.size()>0)
                {
                    apdLotie.setVisibility(View.GONE);
                }

            // Initializing the ViewPagerAdapter
                visitViewPagerAdapter = new AppointmentCardPagerAdapter(getContext(),apointmentModelArrayList,appkeylisyt,myRefappoint);
                appointmentViewPager.setAdapter(visitViewPagerAdapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                dialog.dismiss();

                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });

        if(apointmentModelArrayList.isEmpty())
        {
            apdLotie.setVisibility(View.VISIBLE);

        }
    }

    private void configureChartAppearance() {
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(300f);

        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);



//        String[] xAxisLables = new String[]{"1", "2", "Tue","Wed","Thur","Fri","Sat" };
//
//          chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));



    }

    private BarData createChartData() {

        ArrayList<BarEntry> bpList = new ArrayList<>();
        ArrayList<BarEntry> glucoseList = new ArrayList<>();
        ArrayList<BarEntry> weightList = new ArrayList<>();



//        for (int i = 0; i < MAX_X_VALUE; i++) {
//            bpList.add(new BarEntry(i, 180));
//            glucoseList.add(new BarEntry(i, 110));
//        }
//
//        for (int i = 0; i < 2; i++) {
//
//            weightList.add(new BarEntry(i, 70));
//        }


        //i stand for week day



            // j stand dat post

            for(int j=0;j<vitalModelArrayList.size();j++)
            {

                if(vitalModelArrayList.get(j).getCat().equals("Blood Pressure"))
                {

                    bpList.add(new BarEntry(0, Float.parseFloat(vitalModelArrayList.get(j).getValue())));

                }
                else  if(vitalModelArrayList.get(j).getCat().equals("Glucose Level") )
                {
                    glucoseList.add(new BarEntry(0, Float.parseFloat(vitalModelArrayList.get(j).getValue())));

                }
                else if(vitalModelArrayList.get(j).getCat().equals("Weight") )
                {
                    weightList.add(new BarEntry(0, Float.parseFloat(vitalModelArrayList.get(j).getValue())));

                }

            }



        BarDataSet set1 = new BarDataSet(bpList, GROUP_1_LABEL);
        BarDataSet set2 = new BarDataSet(glucoseList, GROUP_2_LABEL);
        BarDataSet set3 = new BarDataSet(weightList, GROUP_3_LABEL);

        if(bpList.size()>0)
        {

        }
//        set1.setColor(getResources().getColor(R.color.colorAccent));
//        set2.setColor(getResources().getColor(R.color.colorAccentLight));
//        set3.setColor(getResources().getColor(R.color.textColor));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);

        BarData data = new BarData(dataSets);
        chart.setData(data);

        chart.getBarData().setBarWidth(BAR_WIDTH);

        float groupSpace = 1f - ((BAR_SPACE + BAR_WIDTH) * GROUPS);
        chart.groupBars(0, groupSpace, BAR_SPACE);

        chart.invalidate();
        return data;
    }

    private void prepareChartData(BarData data) {

    }

    private void fetchVitalDataFirBase() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("vitalsRecord");
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);

                vitalModelArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());

                    VitalModel imagemodel = snapshot.getValue(VitalModel.class);
                    vitalModelArrayList.add(imagemodel);

                }
                if(vitalModelArrayList.size()>0){
                    BarData data = createChartData();
                    configureChartAppearance();
                    prepareChartData(data);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });
    }


}