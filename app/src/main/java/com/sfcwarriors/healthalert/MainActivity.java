package com.sfcwarriors.healthalert;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.sfcwarriors.healthalert.neumo.Utils;

public class MainActivity extends AppCompatActivity {

    ImageView logo , splash_bg;
    TextView logo_name;
    Animation anim_fadeIn;
    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //background neumorphism Theme;
        Utils.blackIconStatusBar(MainActivity.this,R.color.light_bg);

      IDs();




    }

    public void IDs(){


        lottieAnimationView = findViewById(R.id.animationView);
        logo = findViewById(R.id.logoText);

        lottieAnimationView.addLottieOnCompositionLoadedListener(new LottieOnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                }, 2300);
            }
        });

    }


}