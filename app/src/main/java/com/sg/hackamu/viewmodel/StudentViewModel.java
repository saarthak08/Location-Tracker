package com.sg.hackamu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.sg.hackamu.models.User;
import com.sg.hackamu.repository.StudentRepository;

import java.util.List;

public class StudentViewModel extends AndroidViewModel {

    private StudentRepository studentRepository;
    public StudentViewModel(@NonNull Application application) {
        super(application);
        studentRepository =new StudentRepository();
    }

    public LiveData<List<DataSnapshot>> getAllUsers(){
        return studentRepository.getAllUsers();
    }

    public void removeUser(String key){
        studentRepository.removeUser(key);
    }

    public void addUser(User user,String key){
        studentRepository.addUser(user,key);
    }

}
