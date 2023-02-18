package com.example.quickchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickchat.R;
import com.example.quickchat.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView txt_signin,btn_Sigup;
    CircleImageView profile_image;
    EditText reg_name,reg_email,reg_pass,reg_cpass;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();


        txt_signin=findViewById(R.id.txt_signin);
        profile_image=findViewById(R.id.profile_image);
        reg_cpass=findViewById(R.id.reg_cpass);
        reg_pass=findViewById(R.id.reg_pass);
        reg_email=findViewById(R.id.reg_email);
        reg_name=findViewById(R.id.reg_name);
        btn_Sigup=findViewById(R.id.btn_SignUp);

        btn_Sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=reg_name.getText().toString();
                String password=reg_pass.getText().toString();
                String cpassword=reg_cpass.getText().toString();
                String email=reg_email.getText().toString();
                String status="Hey There I'am Using this Application";

                if(TextUtils.isEmpty(name)|| TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(cpassword)){
                    Toast.makeText(RegistrationActivity.this, "please enter valid Data", Toast.LENGTH_SHORT).show();
                }


                        else if(!email.matches(emailPattern)) {
                            reg_email.setError("please Enter valid email");
                            Toast.makeText(RegistrationActivity.this, "please enter valid email", Toast.LENGTH_SHORT).show();
                        }
                        else if(!password.equals(cpassword)){
                            Toast.makeText(RegistrationActivity.this, "password not match", Toast.LENGTH_SHORT).show();
                        }
                         else if (password.length()<6)   {
                            Toast.makeText(RegistrationActivity.this, "password must be greater than 6 character", Toast.LENGTH_SHORT).show();
                        }
                         else {  auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "user created successfully", Toast.LENGTH_SHORT).show();
                            DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
                            StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());
                            if(imageUri!=null){
                                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isSuccessful()){
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imageURI=uri.toString();
                                                    Users users=new Users(auth.getUid(), name,email,imageURI,status);
                                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                            }
                                                            else {
                                                                Toast.makeText(RegistrationActivity.this, "Error in creating a new  user", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                String status="Hey There I'am Using this Application";
                                imageURI="https://firebasestorage.googleapis.com/v0/b/quick-chat-f6bb4.appspot.com/o/profile_image.png?alt=media&token=e7e39ed6-9dd5-4a87-890f-0da47d4a1a76";
                                Users users=new Users(auth.getUid(),name,email,imageURI,status);
                                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                        }
                                        else {
                                            Toast.makeText(RegistrationActivity.this, "Error in creating a new  user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                        else {
                            Toast.makeText(RegistrationActivity.this, "somthing went wrong", Toast.LENGTH_SHORT).show();
                        }

                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),10);
            }
        });

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                imageUri=data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}