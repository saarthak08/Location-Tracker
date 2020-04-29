package com.sg.hackamu.viewmodel.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import java9.util.concurrent.CompletableFuture;

public class StudentRepository {

    @Inject
    public FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<DataSnapshot> users;
    private List<DataSnapshot> allStudentsList;
    private MutableLiveData<List<DataSnapshot>> mutableLiveData = new MutableLiveData<>();

    public StudentRepository() {
        allStudentsList = new ArrayList<>();
        firebaseDatabase = FirebaseUtils.getDatabase();
        databaseReference = firebaseDatabase.getReference();
    }

    public CompletableFuture<List<DataSnapshot>> getAllStudentsListInstant() {
        databaseReference.child("students_list").keepSynced(true);
        CompletableFuture<List<DataSnapshot>> completableFuture = new CompletableFuture<>();
        databaseReference.child("students_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    allStudentsList.add(dataSnapshot1);
                }
                completableFuture.complete(allStudentsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return completableFuture;
    }


    public LiveData<List<DataSnapshot>> getAllStudents() {
        databaseReference.child("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    users.add(dataSnapshot1);
                }
                mutableLiveData.postValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mutableLiveData;
    }

    public void deleteStudent(String key) {
        databaseReference.child("students").child(key).removeValue();
    }

    public void addStudent(Student student, String key) {
        databaseReference.child("students_list").keepSynced(true);
        databaseReference.child("students").child(key).setValue(student);
    }


    public void addStudentInfoToFacultiesList(String id,String node) {
        databaseReference.child("students_list").child(node).setValue(id);
    }

    public void deleteStudentInfoFromFacultiesList(String key) {
        databaseReference.child("students_list").child(key).removeValue();
    }
}
