package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.quickchat.Activity.HomeActivity;
import com.example.quickchat.Activity.MainActivity;
import com.example.quickchat.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingActivity extends AppCompatActivity {

    CircleImageView setting_image;
    EditText setting_name, setting_status;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ImageView save;
    Uri setImageURI;
    String email;
    ProgressDialog progressDialog;
    Spinner spinner;
    String[] algorithm = {"SHA", "SHA125", "MD5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);



        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setting_image = findViewById(R.id.setting_image);
        setting_name = findViewById(R.id.setting_name);
        setting_status = findViewById(R.id.setting_status);
        save = findViewById(R.id.save);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();


                setting_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(image).into(setting_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
//                Toast.makeText(SettingActivity.this, "update successfully", Toast.LENGTH_SHORT).show();
                String name = setting_name.getText().toString();
                String status = setting_status.getText().toString();

                if (setImageURI != null) {
                    storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(auth.getUid(), name, email, finalImageUri, status);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }
                            });
                        }
                    });
                } else {
                    storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(auth.getUid(), name, email, finalImageUri, status);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }
                            });
                        }
                    });
                }

//                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                setImageURI = data.getData();
                setting_image.setImageURI(setImageURI);
            }
        }

        spinner = findViewById(R.id.algo_type);
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SettingActivity.this,R.layout.item_file,algorithm);
//        adapter.setDropDownViewResource(R.layout.item_file);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
//                String value=parent.getItemAtPosition(position).toString();
//                Toast.makeText(SettingActivity.this, algorithm[position] , Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.spinnerlist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String data=adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SettingActivity.this, "selected", Toast.LENGTH_SHORT).show();
                Toast.makeText(SettingActivity.this, "data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}