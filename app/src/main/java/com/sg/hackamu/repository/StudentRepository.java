package com.sg.hackamu.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class StudentRepository {

    @Inject
    public FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<DataSnapshot> users;
    private MutableLiveData<List<DataSnapshot>> mutableLiveData=new MutableLiveData<>();

    public StudentRepository(){
        firebaseDatabase= FirebaseUtils.getDatabase();
        databaseReference=firebaseDatabase.getReference();
        users=new ArrayList<>();
    }

    public LiveData<List<DataSnapshot>> getAllStudents(){
        databaseReference.child("students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                users.add(dataSnapshot);
                mutableLiveData.setValue(users);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                users.remove(dataSnapshot);
                mutableLiveData.setValue(users);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return mutableLiveData;
    }

    public void deleteStudent(String key){
        databaseReference.child("students").child(key).removeValue();
    }

    public void addStudent(Student student, String key){
        databaseReference.child("students").child(key).setValue(student);
    }
}
