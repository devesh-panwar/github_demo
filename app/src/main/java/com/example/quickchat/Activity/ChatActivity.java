package com.example.quickchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickchat.Adapter.MessaagesAdater;
import com.example.quickchat.ModelClass.Messages;
import com.example.quickchat.ModelClass.Users;
import com.example.quickchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReciverImage,ReciverUID,ReciverName,senderUID;
    CircleImageView profileImage;
    TextView reciverName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    Users users;


     public  static String SImage;
     public static String rImage;

     CardView sendBtn;
     EditText editMessage;

     String senderRoom,reciverRoom;

     RecyclerView messageAdater;
     ArrayList<Messages> messagesArrayList;


     MessaagesAdater adater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        ReciverName=getIntent().getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUID=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();

        profileImage=findViewById(R.id.profile_image);
        reciverName=findViewById(R.id.reciverName);

        messageAdater=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdater.setLayoutManager(linearLayoutManager);
        adater=new MessaagesAdater(ChatActivity.this,messagesArrayList);
        messageAdater.setAdapter(adater);


        sendBtn=findViewById(R.id.sendBtn);
        editMessage=findViewById(R.id.editMessage);

//        Picasso.get().load("ReciverImage").into(profileImage);
        Picasso.get().load(ReciverImage).placeholder(R.drawable.profile).into(profileImage);

        reciverName.setText(""+ReciverName);
        senderUID = firebaseAuth.getUid();

        senderRoom=senderUID+ReciverUID;
        reciverRoom=ReciverUID+senderUID;


        DatabaseReference reference=database.getReference().child("user").child(FirebaseAuth.getInstance().getUid());
        DatabaseReference chatReference=database.getReference().child("chats").child(senderRoom).child("messages");





        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                    adater.notifyDataSetChanged();
            }@Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SImage=snapshot.child("imageUri").getValue().toString();
                rImage=ReciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final MediaPlayer mp = MediaPlayer.create(this,R.raw.message_send);
        final MediaPlayer fail=MediaPlayer.create(this,R.raw.failed);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message =editMessage.getText().toString();
                if(message.isEmpty()){
                    fail.start();
                    Toast.makeText(ChatActivity.this, "PLEASE ENTER VALID MESSAGE", Toast.LENGTH_SHORT).show();
                      return;
                }
                else{ editMessage.setText("");
                    mp.start();
                    Date date =new Date();
                    Messages messages =new Messages(message, senderUID, date.getTime());

                    database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("chats").child(reciverRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });

                }
            }
        });
        final MediaPlayer mp1 = MediaPlayer.create(this,R.raw.message_recieve);

//Messages messages=new Messages();
//messages.execute();
//
//        class Messages extends AsyncTask<Void,Void,Void>{
//            @Override
//            protected void doInBackground(Void void){
//return null;
//            }
//        }

         chatReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
 if(ReciverUID== firebaseAuth.getUid()){
     Notification();
     mp1.start();
 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });




    }

    public void Notification(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
       NotificationManager manager=getSystemService(NotificationManager.class);
       manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"n").setContentText("quick chat").setSmallIcon(R.drawable.logo).setAutoCancel(true).setContentText("you got a message");
        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }

    int ITEM_SEND=1;
    public class Notice extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if( viewType!=ITEM_SEND){
                Notification();

            }return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return messagesArrayList.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}