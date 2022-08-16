package com.sfcwarriors.healthalert.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.sfcwarriors.healthalert.R;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CaseHistoryAdapter extends RecyclerView.Adapter<CaseHistoryAdapter.ViewHolder> {

    ArrayList<CaseHistoryModel> caseHistoryModelArrayList;
    ArrayList<String> keyList;
    Context context;
    DatabaseReference myRef;

    public CaseHistoryAdapter(ArrayList<CaseHistoryModel> caseHistoryModelArrayList, ArrayList<String> keyList, Context context, DatabaseReference myRef) {
        this.caseHistoryModelArrayList = caseHistoryModelArrayList;
        this.keyList = keyList;
        this.context = context;
        this.myRef = myRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.casehistory_card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Context mCon=holder.itemView.getContext();
        if(position%2==0)
        {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mCon, R.color.greenLight));
        }
        else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mCon, R.color.lightBlue));

        }
        String title=caseHistoryModelArrayList.get(position).getDname();
        String des=caseHistoryModelArrayList.get(position).getDetail();
        String date=caseHistoryModelArrayList.get(position).getDate();
        holder.chtitle.setText(title);
        holder.chdes.setText(des);

        String key=keyList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(title+"\b "+date)
                        .setContentText(title)
                        .setContentText(des)
                        .setConfirmText("Yes")
                        .setConfirmText("Delete!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                myRef.child(key).removeValue();
                                Toast.makeText(mCon, "Data deleted", Toast.LENGTH_SHORT).show();
                                sDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseHistoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView chtitle,chdes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.mCard);
            chtitle=itemView.findViewById(R.id.chtitle);
            chdes=itemView.findViewById(R.id.chdes);
        }
    }
}