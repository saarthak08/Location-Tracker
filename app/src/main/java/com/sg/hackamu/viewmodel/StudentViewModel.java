package com.sg.hackamu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.viewmodel.repository.StudentRepository;

import java.util.List;
import java.util.concurrent.Future;

import java9.util.concurrent.CompletableFuture;

public class StudentViewModel extends AndroidViewModel {

    private StudentRepository studentRepository;
    public StudentViewModel(@NonNull Application application) {
        super(application);
        studentRepository =new StudentRepository();
    }

    public LiveData<List<DataSnapshot>> getAllStudents(){
        return studentRepository.getAllStudents();
    }

    public void deleteStudent(String key){
        studentRepository.deleteStudent(key);
    }

    public void addStudent(Student student, String key){
        studentRepository.addStudent(student,key);
    }

    public CompletableFuture<List<DataSnapshot>> getAllInstantStudentsList() {
        return studentRepository.getAllStudentsListInstant();
    }


    public void addStudentToFacultiesList(String id,String node) {
        studentRepository.addStudentInfoToFacultiesList(id,node);
    }

    public void deleteStudentFromFacultiesList(String key) {
        studentRepository.deleteStudentInfoFromFacultiesList(key);
    }
}
