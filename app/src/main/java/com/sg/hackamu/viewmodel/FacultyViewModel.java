package com.sg.hackamu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.viewmodel.repository.FacultyRepository;

import java.util.List;

public class FacultyViewModel extends AndroidViewModel {
    private FacultyRepository facultyRepository;

    public FacultyViewModel(@NonNull Application application) {
        super(application);
        facultyRepository=new FacultyRepository();
    }

    public LiveData<List<DataSnapshot>> getAllFaculties(){
        return facultyRepository.getAllFaculties();
    }

    public void addFaculty(Faculty faculty,String key){
        facultyRepository.addFaculty(faculty,key);
    }

    public void deleteFaculty(String key){
        facultyRepository.deleteFaculty(key);
    }
}
