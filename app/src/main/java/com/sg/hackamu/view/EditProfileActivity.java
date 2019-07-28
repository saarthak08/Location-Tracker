package com.sg.hackamu.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;

public class EditProfileActivity extends AppCompatActivity {
    private Student student;
    private Faculty faculty;
    private ScrollView scrollView;
    private boolean isuser;
    private TextView email;
    private EditText nameET;
    private EditText departmentET;
    private EditText collegeET;
    private EditText idET;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private Button saveButton;
    private Button cancelButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference=firebaseDatabase.getReference();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i = getIntent();
        email=findViewById(R.id.emailTv);
        nameET=findViewById(R.id.nameET);
        saveButton=findViewById(R.id.saveButton);
        cancelButton=findViewById(R.id.cancelButton);
        imageView=findViewById(R.id.imageViewEP);
        departmentET=findViewById(R.id.departmentET);
        scrollView=findViewById(R.id.scrollViewEP);
        collegeET=findViewById(R.id.collegeET);
        idET=findViewById(R.id.idET);
        nameET.setText(firebaseUser.getDisplayName());
        if (i.hasExtra("student")) {
            student = i.getParcelableExtra("student");
            isuser=true;
            if(student.getEmail()==null) {
                email.setText("Phone Number: "+student.getPhoneno());
            }
            else{
            email.setText("Email:" +student.getEmail() );}
            departmentET.setText(student.getDepartment());
            collegeET.setText(student.getCollege());
            idET.setText(student.getEnno());
        }
        if(student ==null)
        {
            faculty=i.getParcelableExtra("faculty");
            isuser=false;
            if(faculty.getEmail()==null){
                email.setText("Phone Number: "+faculty.getPhoneno());
            }
            else{
            email.setText("Email: "+faculty.getEmail());}
            departmentET.setText(faculty.getDepartment());
            collegeET.setText(faculty.getCollege());
            idET.setText(faculty.getEmployeeid());
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isuser){
                    student.setCollege(collegeET.getText().toString().trim());
                    student.setName(nameET.getText().toString().trim());
                    student.setDepartment(departmentET.getText().toString().trim());
                    student.setEnno(idET.getText().toString().trim());
                    databaseReference.child("students").child(firebaseUser.getUid()).setValue(student);
                    Snackbar.make(findViewById(android.R.id.content),"Saved!",Snackbar.LENGTH_SHORT).show();
                }
                else{
                    faculty.setCollege(collegeET.getText().toString().trim());
                    faculty.setName(nameET.getText().toString().trim());
                    faculty.setDepartment(departmentET.getText().toString().trim());
                    faculty.setEmployeeid(idET.getText().toString().trim());
                    databaseReference.child("faculties").child(firebaseUser.getUid()).setValue(student);
                    Snackbar.make(findViewById(android.R.id.content),"Saved!",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
