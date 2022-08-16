package com.sfcwarriors.healthalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sfcwarriors.healthalert.model.UserDataModel;
import com.sfcwarriors.healthalert.neumo.Utils;

import java.util.Map;

import soup.neumorphism.NeumorphButton;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layout_reg;
    Animation anim_fadeIn;
    TextView login_btn;

    EditText uname,uemal,uage,uaddress,uphone;
    TextInputEditText upassword;
    NeumorphButton signup;
    LottieDialog dialog;
    RadioGroup gender;
    UserDataModel userDataModel;
    RadioButton   radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Utils.blackIconStatusBar(RegisterActivity.this,R.color.light_bg);

        init();

        signup.setOnClickListener(this);

        regPage_anim();
    }

    public void init(){

        layout_reg = findViewById(R.id.layoutReg);
        login_btn = findViewById(R.id.loginBtn);

        uname = findViewById(R.id.uName);
        uemal = findViewById(R.id.uemail);
        uage = findViewById(R.id.uage);
        uaddress = findViewById(R.id.uaddress);
        uphone = findViewById(R.id.uphone);

        upassword = findViewById(R.id.upassword);
        gender = findViewById(R.id.ugender);
        signup = findViewById(R.id.signup);


         dialog = new LottieDialog(this)
                .setAnimation(R.raw.loading)
                .setAutoPlayAnimation(true)
                .setDialogHeightPercentage(.2f)
                 .setAnimationViewHeight(20)
                 .setAnimationViewHeight(20)
                .setAnimationRepeatCount(LottieDialog.INFINITE)
                .setMessage("Loading...");


    }

    public void regPage_anim(){

        anim_fadeIn = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout_reg.setVisibility(View.VISIBLE);
                layout_reg.setAnimation(anim_fadeIn);
            }
        }, 100);

    }
    @Override
    public void onClick(View view) {
        if(view==signup)
        {

            if(uname.getText().toString().isEmpty())
            {
                uname.setError("Enter an User Name First");

                return;
            }
            if(uemal.getText().toString().isEmpty())
            {
                uemal.setError("Enter an Email First");
                return;
            }

            if(!uemal.getText().toString().contains("@"))
            {
                uemal.setError("Enter an Correct Email First");
                return;
            }
            if(uage.getText().toString().isEmpty())
            {
                uage.setError("Enter an Gender First");

                return;
            }
            if(uaddress.getText().toString().isEmpty())
            {
                uaddress.setError("Enter an Address First");

                return;
            }
            if(uphone.getText().toString().isEmpty())
            {
                uphone.setError("Enter an Phone No First");

                return;
            }
            if(uphone.getText().length()<11)
            {
                uphone.setError("Phone No Length must be 11");

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

            // get selected radio button from radioGroup
            int selectedId = gender.getCheckedRadioButtonId();

            // find the radiobutton by returned id
               radioButton = (RadioButton) findViewById(selectedId);



            dialog.show();
            firebaseAuth();
        }

    }
    public void firebaseAuth(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(uemal.getText().toString(), upassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();


                            userDataModel=new UserDataModel(uname.getText().toString(),
                                    uemal.getText().toString(),
                                    uage.getText().toString(),
                                    uaddress.getText().toString(),
                                    uphone.getText().toString(),
                                    upassword.getText().toString(),
                                    radioButton.getText().toString(),
                                    1,"",task.getResult().getUser().getUid().toString());
                            inserData(userDataModel);

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Registration failed! "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void inserData(UserDataModel model){
        FirebaseDatabase database;
        DatabaseReference myRef;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");


        myRef.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();

                Log.e("Errror",e.getMessage());
                dialog.dismiss();

            }
        });
    }
}