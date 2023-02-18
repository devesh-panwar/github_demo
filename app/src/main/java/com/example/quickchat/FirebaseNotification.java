//package com.example.quickchat;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.connection.ConnectionTokenProvider;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import okhttp3.internal.Util;
//
//public class FirebaseNotification extends FirebaseMessagingService {
//    private Util util=new Util();
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage message) {
//        super.onMessageReceived(message);
//    }
//
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        updateToken(s);
//        super.onNewToken(token);
//    }
////    private void updateToken(String token) {
////        DatabaseReference databaseReferencefirebae = FirebaseDatabase.getInstance().getReference("users").child(util.getUID());
//        Map<String, Object> map = new HashMap<>();
//        map.put("token",token);
//        databaseReferencefirebae.updateChildren(map);
//
//
//    }
//}
