package com.sfcwarriors.healthalert.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieDrawable;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.FeedBAckModel;

import soup.neumorphism.NeumorphButton;

public class FeedbackFragment extends Fragment {

    NeumorphButton btn;

    EditText name,email,phone,text;

    View view;
    LottieDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_feedback, container, false);

         btn=view.findViewById(R.id.feedbackBtn);
        name=view.findViewById(R.id.atNameF);
        email=view.findViewById(R.id.atEmailF);
        phone=view.findViewById(R.id.atPhoneF);
        text=view.findViewById(R.id.textArea);

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
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(name.getText().toString().isEmpty())
                {
                    name.setError("Fill First");
                    return;
                }
                if(email.getText().toString().isEmpty())
                {
                    email.setError("Fill First");
                    return;
                }
                if(phone.getText().toString().isEmpty())
                {
                    phone.setError("Fill First");
                    return;
                }
                if(text.getText().toString().isEmpty())
                {
                    text.setError("Fill First");
                    return;
                }
                dialog.show();
                FeedBAckModel feedBAckModel=new FeedBAckModel(name.getText().toString(),email.getText().toString(),phone.getText().toString(),text.getText().toString());

                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

                DatabaseReference databaseReference=firebaseDatabase.getReference().child("feedbacks");

                databaseReference.push().setValue(feedBAckModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        name.setText("");
                        email.setText("");
                        phone.setText("");
                        text.setText("");

                        Toast.makeText(getContext(), "Data inseted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                        Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();

                    }
                }) ;



            }
        });


         return view;
    }
}