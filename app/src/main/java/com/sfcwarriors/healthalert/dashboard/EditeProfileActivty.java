package com.sfcwarriors.healthalert.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sfcwarriors.healthalert.R;
import com.sfcwarriors.healthalert.model.UserDataModel;

import java.io.IOException;
import java.util.UUID;

import soup.neumorphism.NeumorphButton;

public class EditeProfileActivty extends AppCompatActivity {



    EditText uname,uemal,uage,uaddress,uphone;
    TextInputEditText upassword;
    TextView uname1,uemal1;
    NeumorphButton userSaveBtn;
    LottieDialog dialog;
    RadioGroup gender;
    UserDataModel userDataModel;
    RadioButton radioButton;

    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_profile);

        init();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    public void init(){


        imageView=findViewById(R.id.proo);


        uname = findViewById(R.id.userName2);
        uemal = findViewById(R.id.userEmail2);

        uname1 = findViewById(R.id.userName);
        uemal1 = findViewById(R.id.userEmail);

        uage = findViewById(R.id.userAge);
        uaddress = findViewById(R.id.userAdress);
        uphone = findViewById(R.id.userPhone);

        upassword = findViewById(R.id.userPass);
        gender = findViewById(R.id.userGander);
        userSaveBtn = findViewById(R.id.userSaveBtn);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dialog = new LottieDialog(this)
                .setAnimation(R.raw.loading)
                .setAutoPlayAnimation(true)
                .setDialogHeightPercentage(.2f)
                .setAnimationViewHeight(20)
                .setAnimationViewHeight(20)
                .setAnimationRepeatCount(LottieDialog.INFINITE)
                .setMessage("Loading...");

        fetchDataFirBase();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        userSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();


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




                String imagename=UUID.randomUUID().toString();
                uploadImage(imagename);

                dialog.show();
                userDataModel=new UserDataModel(uname.getText().toString(),
                        uemal.getText().toString(),
                        uage.getText().toString(),
                        uaddress.getText().toString(),
                        uphone.getText().toString(),
                        upassword.getText().toString(),
                        radioButton.getText().toString(),
                        1,imagename,firebaseAuth.getUid());
                inserData(userDataModel);
            }
        });
    }

    public void getImage(String imageName){
        storageReference.child("images/"+ imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(getApplicationContext()).load(uri).into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
    private void uploadImage(String imagename) {

        if(filePath != null)
        {

            dialog.show();

            StorageReference ref = storageReference.child("images/"+ imagename);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void inserData(UserDataModel model){
        FirebaseDatabase database;
        DatabaseReference myRef;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.child(auth.getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(EditeProfileActivty.this, "Data Updated", Toast.LENGTH_SHORT).show();
                fetchDataFirBase();
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

    public void fetchDataFirBase() {

        FirebaseDatabase database;
        DatabaseReference myRef;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");

        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        myRef.orderByChild("id").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);


                UserDataModel userDataModel;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("dfdfdf",snapshot.getKey().toString());


                    userDataModel = snapshot.getValue(UserDataModel.class);

                    uname.setText(userDataModel.getUname());
                    uemal.setText(userDataModel.getUemal());

                    uname1.setText(userDataModel.getUname());
                    uemal1.setText(userDataModel.getUemal());

                    uaddress.setText(userDataModel.getUaddress());
                    uphone.setText(userDataModel.getUphone());
                    uage.setText(userDataModel.getUage());
                    upassword.setText(userDataModel.getUpassword());


                    getImage(userDataModel.getUserProfileIImage());


                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);


                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("uname", userDataModel.getUname());
                    editor.putString("uemail", userDataModel.getUemal());
                    editor.putString("uaddress", userDataModel.getUaddress());
                    editor.putString("uphone", userDataModel.getUphone());
                    editor.putString("ugender", userDataModel.getGender());
                    editor.putString("uid", userDataModel.getId());
                    editor.putString("uprofile", userDataModel.getUserProfileIImage());
                    editor.putString("upassword", userDataModel.getUpassword());
                    editor.commit();
                }



                dialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Data not fetch", "Failed to read value.", error.toException());
            }
        });
    }

}