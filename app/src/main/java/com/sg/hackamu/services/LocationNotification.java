package com.sg.hackamu.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.MapsActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LocationNotification extends Service {
    private FirebaseAuth firebaseAuth;
    Faculty faculty;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String uuid;
    NotificationManagerCompat notificationManager;
    int notificationId=2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseUtils.getDatabase();
        databaseReference=firebaseDatabase.getReference();
        databaseReference.child("geocordinates").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    uuid = dataSnapshot.getKey();
                    databaseReference.child("faculties").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (uuid.equals(dataSnapshot.getKey())) {
                                faculty = dataSnapshot.getValue(Faculty.class);
                                String name = (String) dataSnapshot.child("name").getValue();
                                showNotification(name, faculty);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                notificationManager.cancel(notificationId);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int showNotification(String name,Faculty faculty)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence names ="Location Updates";
            String description = name+" updated realtime location.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL", names, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("faculty",faculty);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Location Updates")
                .setVibrate(new long[]{1000, 1000})
                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setContentText(name+" updated realtime location.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationId, builder.build());
        return notificationId;

    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }
}
