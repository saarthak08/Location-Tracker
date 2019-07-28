package com.sg.hackamu.viewmodel.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FacultyRepository {

    @Inject
    public FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<DataSnapshot> users;
    private MutableLiveData<List<DataSnapshot>> mutableLiveData=new MutableLiveData<>();

    public FacultyRepository(){
        firebaseDatabase= FirebaseUtils.getDatabase();
        databaseReference=firebaseDatabase.getReference();
    }

    public LiveData<List<DataSnapshot>> getAllFaculties(){
        databaseReference.child("faculties").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users=new ArrayList<>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
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

    public void deleteFaculty(String key){
        databaseReference.child("faculties").child(key).removeValue();
    }

    public void addFaculty(Faculty faculty,String key){
        databaseReference.child("faculties").child(key).setValue(faculty);
    }

}
