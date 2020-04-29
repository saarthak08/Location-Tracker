package com.sg.hackamu.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivityLauncherBinding;
import com.sg.hackamu.view.faculties.FacultyLogin;
import com.sg.hackamu.view.faculties.FacultyMainActivity;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.view.students.StudentLogin;
import com.sg.hackamu.view.students.StudentMainActivity;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class LauncherActivity extends AppCompatActivity {
    private ActivityLauncherBinding launcherBinding;
    private Button fcbutton;
    private Button stbutton;
    FirebaseUser firebaseUser;
    StudentViewModel studentViewModel;
    boolean isuser;
    Faculty faculty;
    Student student;
    FacultyViewModel facultyViewModel;
    FirebaseAuth firebaseAuth;
    boolean signin=false;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseUtils.getDatabase();
        databaseReference=firebaseDatabase.getReference();
        facultyViewModel= ViewModelProviders.of(this).get(FacultyViewModel.class);
        studentViewModel=ViewModelProviders.of(this).get(StudentViewModel.class);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        if (firebaseUser!= null) {
            try{
               studentViewModel.getAllStudents().observe(this, new Observer<List<DataSnapshot>>() {
                    @Override
                    public void onChanged(List<DataSnapshot> dataSnapshots) {
                        for (DataSnapshot dataSnapshot : dataSnapshots) {
                            if (firebaseUser.getUid().equals(dataSnapshot.getKey())) {
                                student = dataSnapshot.getValue(Student.class);
                                signin=true;
                                if (!firebaseUser.isEmailVerified() && student.getEmail()!=null) {
                                    Intent intent = new Intent(LauncherActivity.this, VerifyActivity.class);
                                    intent.putExtra("student", student);
                                    startActivity(intent);
                                    LauncherActivity.this.finish();
                                } else {
                                    startActivity(new Intent(LauncherActivity.this, StudentMainActivity.class));
                                    LauncherActivity.this.finish();
                                }
                            }
                        }
                    }
               });
               facultyViewModel.getAllFaculties().observe(this, new Observer<List<DataSnapshot>>() {
                   @Override
                   public void onChanged(List<DataSnapshot> dataSnapshots) {
                       for (DataSnapshot dataSnapshot : dataSnapshots) {
                           if (firebaseUser.getUid().equals(dataSnapshot.getKey())) {
                               signin=true;
                               faculty = dataSnapshot.getValue(Faculty.class);
                               if (!firebaseUser.isEmailVerified() && faculty.getEmail()!=null) {
                                   Intent intent = new Intent(LauncherActivity.this, VerifyActivity.class);
                                   intent.putExtra("faculty", faculty);
                                   startActivity(intent);
                                   LauncherActivity.this.finish();
                               } else {
                                   startActivity(new Intent(LauncherActivity.this, FacultyMainActivity.class));
                                   LauncherActivity.this.finish();
                               }
                           }
                       }
                   }
               });
            }
          catch (Exception e)
            {
                Log.d("LauncherException",e.getMessage());
            }
            }else {
            setContentView(R.layout.activity_launcher);
            launcherBinding = DataBindingUtil.setContentView(LauncherActivity.this, R.layout.activity_launcher);
            launcherBinding.setClickHandlers(new LauncherActivityClickHandlers());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public class LauncherActivityClickHandlers{
        public void onFacultyButtonClicked(View view)
        {
            startActivity(new Intent(LauncherActivity.this, FacultyLogin.class));
            LauncherActivity.this.finish();
        }


        public void onStudentButtonClicked(View view)
        {
            startActivity(new Intent(LauncherActivity.this, StudentLogin.class));
            LauncherActivity.this.finish();
        }
    }
}