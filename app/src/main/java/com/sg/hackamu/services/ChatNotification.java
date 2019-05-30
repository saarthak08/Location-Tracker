package com.sg.hackamu.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.ChatActivity;
import com.sg.hackamu.MapsActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.models.ChatMessage;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.User;
import com.sg.hackamu.students.MainActivity;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ChatNotification extends Service {
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    NotificationManagerCompat notificationManager;
    int notificationId=3;
    User user;
    Faculty faculty;
    boolean isuser;

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
        databaseReference.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot d:dataSnapshot.getChildren())
                {
                    databaseReference.child("chats").child(d.getKey()).child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            final ChatMessage message=dataSnapshot.getValue(ChatMessage.class);
                            if(!(dataSnapshot.getValue(ChatMessage.class).isRead())&&!ChatActivity.running)
                            {
                                final String uuid=d.getKey();
                                databaseReference.child("students").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if (uuid.equals(dataSnapshot.getKey())) {
                                            user = dataSnapshot.getValue(User.class);
                                            isuser = true;
                                            showNotification(user,faculty,message,isuser);
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
                                databaseReference.child("faculties").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if (uuid.equals(dataSnapshot.getKey())) {
                                            if (faculty == null) {
                                                faculty = dataSnapshot.getValue(Faculty.class);
                                                isuser = false;
                                                showNotification(user,faculty,message,isuser);
                                            }
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
                            else {
                                if (notificationManager != null) {
                                    notificationManager.cancel(notificationId);
                                }
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int showNotification(User user,Faculty faculty,ChatMessage message,boolean isuser)
    {
        int requestID = (int) System.currentTimeMillis();
        if(isuser)
        {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence names ="New Message";
            String description = user.getName();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL", names, importance);
            channel.setDescription(description+message.getMessageText());
            channel.setName(names);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user",user);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), requestID,new Intent[]{new Intent(getApplicationContext(), FacultyMainActivity.class),intent}, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                .setSmallIcon(R.drawable.ic_nav_message)
                .setContentTitle(user.getName())
                .setVibrate(new long[]{1000, 1000})
                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setContentText(message.getMessageText())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationId, builder.build());
        this.user=null;
        return notificationId;
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence names ="New Message";
                String description = faculty.getName();
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("CHANNEL", names, importance);
                channel.setDescription(description+message.getMessageText());
                channel.setName(names);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("faculty",faculty);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), requestID,new Intent[]{new Intent(getApplicationContext(), MainActivity.class),intent}, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                    .setSmallIcon(R.drawable.ic_nav_message)
                    .setContentTitle(faculty.getName())
                    .setVibrate(new long[]{500,500})
                    .setColorized(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setContentText(message.getMessageText())
                    .setPriority(NotificationCompat.PRIORITY_HIGH).setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            notificationManager = NotificationManagerCompat.from(getApplicationContext());
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);
            notificationManager.notify(notificationId, builder.build());
            this.faculty=null;
            return notificationId;

        }

    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        PendingIntent test = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }
}
