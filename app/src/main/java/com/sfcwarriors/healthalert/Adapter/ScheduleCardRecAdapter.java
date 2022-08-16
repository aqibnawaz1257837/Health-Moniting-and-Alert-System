package com.sfcwarriors.healthalert.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.ScheduleDataModel;

import java.util.ArrayList;

public class ScheduleCardRecAdapter extends RecyclerView.Adapter<ScheduleCardRecAdapter.ViewHolder> {



    ArrayList<ScheduleDataModel> scheduleDataModelsList;
    ArrayList<String> keyList;
    DatabaseReference myRef;;
    Context context;

    public ScheduleCardRecAdapter(ArrayList<ScheduleDataModel> scheduleDataModelsList, ArrayList<String> keyList, DatabaseReference myRef, Context context) {
        this.scheduleDataModelsList = scheduleDataModelsList;
        this.keyList = keyList;
        this.myRef = myRef;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleCardRecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduler_card,parent,false);


        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ScheduleCardRecAdapter.ViewHolder holder, int position) {


        String sched_sdate11 = scheduleDataModelsList.get(position).getStartDate();
        String[] arr1 = sched_sdate11.split("/");
        String s=arr1[0];

        String sched_sdate12 = scheduleDataModelsList.get(position).getEndDate();
        String[] arr2 = sched_sdate12.split("/");
        String s1=arr2[0];
        holder.sched_edate.setText(s1);
        holder.sched_sdate.setText(s);


        holder.sched_medname.setText(scheduleDataModelsList.get(position).getMedName());
        holder.sched_reptime.setText(scheduleDataModelsList.get(position).getNoRepeat());
        holder.sched_gaptime.setText(scheduleDataModelsList.get(position).getGapeTime());
    }

    @Override
    public int getItemCount() {
        return scheduleDataModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sched_sdate, sched_edate, sched_medname, sched_reptime, sched_gaptime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sched_sdate = itemView.findViewById(R.id.schedSdate);
            sched_edate = itemView.findViewById(R.id.schedEDate);
            sched_medname = itemView.findViewById(R.id.schedMedName);
            sched_reptime = itemView.findViewById(R.id.schedRepTime);
            sched_gaptime = itemView.findViewById(R.id.schedGapTime);
        }
    }
}