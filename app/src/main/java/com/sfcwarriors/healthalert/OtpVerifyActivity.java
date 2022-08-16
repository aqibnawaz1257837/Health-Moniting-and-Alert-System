package com.sfcwarriors.healthalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.sfcwarriors.healthalert.neumo.Utils;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class OtpVerifyActivity extends AppCompatActivity {

    LottieAnimationView lotteiAnim;
    LinearLayout opt_layout;
    GifImageView verified_anim;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        //nuemorphism theme;
        Utils.blackIconStatusBar(OtpVerifyActivity.this,R.color.light_bg);
        /*lotteiAnim = findViewById(R.id.otpAnim);
        lotteiAnim.enableMergePathsForKitKatAndAbove(false);*/

        optPage_ids();

    }

    public void optPage_ids(){
        opt_layout = findViewById(R.id.otpLayout);
        verified_anim = findViewById(R.id.verifiedAnim);
    }

    public void goVerified(View view) {

        opt_layout.setVisibility(View.GONE);
        verified_anim.setVisibility(View.VISIBLE);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent goLogin = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(goLogin);
            }
        },4800);

    }

}