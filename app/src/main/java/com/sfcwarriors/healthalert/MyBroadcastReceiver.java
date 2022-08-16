package com.sfcwarriors.healthalert;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.sfcwarriors.healthalert.AlarmData.activity.AlarmActivity;
import com.sfcwarriors.healthalert.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.notification);
        mp.start();
        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("TITLE", "Remainder");
        i.putExtra("DESC", "Your Appointment has been schdule Now");
        i.putExtra("DATE", "");
        i.putExtra("TIME", "");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
    }
}