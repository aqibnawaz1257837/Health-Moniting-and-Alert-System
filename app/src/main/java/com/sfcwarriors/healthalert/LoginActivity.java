package com.sfcwarriors.healthalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sfcwarriors.healthalert.neumo.Utils;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.internal.util.CanvasCompat;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layout_login;
    Animation anim_fadeIn;
    TextView reg_btn;


    EditText uemail;

    TextInputEditText upassword;
    NeumorphButton loginbutton;
    LottieDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //nuemorphism theme;
        Utils.blackIconStatusBar(LoginActivity.this,R.color.light_bg);

        loginPage_ids();
        loginPage_anim();

        loginbutton.setOnClickListener(this);
    }

    public void loginPage_ids(){

        layout_login = findViewById(R.id.layoutLogin);
        reg_btn = findViewById(R.id.regBtn);

        uemail = findViewById(R.id.uemail);
        upassword = findViewById(R.id.upassword);
        loginbutton = findViewById(R.id.loginbutton);
         dialog = new LottieDialog(this)
                .setAnimation(R.raw.loading)
                .setAnimationRepeatCount(LottieDrawable.INFINITE)
                .setAutoPlayAnimation(true)
                .setTitle("Please Wait")
                .setTitleColor(ContextCompat.getColor(this,R.color.textColor))
                 .setDialogHeightPercentage(.25f)
                .setMessage("Loading...")
                .setMessageColor(ContextCompat.getColor(this,R.color.textColor))
                .setDialogBackground(Color.TRANSPARENT)

                .setCancelable(false);


    }




    @Override
    public void onClick(View view) {

        if(view==loginbutton)
        {
            if(uemail.getText().toString().isEmpty())
            {
                uemail.setError("Enter an Email First");
                return;
            }

            if(!uemail.getText().toString().contains("@"))
            {
                uemail.setError("Enter an Correct Email First");
                return;
            }

            if(upassword.getText().toString().isEmpty())
            {
                upassword.setError("Enter an Password First");

                return;
            }
            if(upassword.getText().length()<6)
            {
                upassword.setError("Week Passowrd");

                return;
            }
        }

        firebaseAuth();

    }

    public void firebaseAuth(){
        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(uemail.getText().toString(), upassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                          //  inserData(userDataModel);

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Login Failed failed! "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }
    public void loginPage_anim(){

        anim_fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout_login.setVisibility(View.VISIBLE);
                layout_login.setAnimation(anim_fadeIn);
            }
        }, 100);

    }

    public void goReg(View view) {
        Intent goReg = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(goReg);
    }
}