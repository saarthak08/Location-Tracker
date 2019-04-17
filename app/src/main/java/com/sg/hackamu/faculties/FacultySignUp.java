package com.sg.hackamu.faculties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivityFacultySignUpBinding;
//import com.sg.hackamu.model.Faculty;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;

public class FacultySignUp extends AppCompatActivity {

    private Button signUpButton;
    private EditText email;
    public static EditText name;
    private EditText password;
    private EditText department;
    private ActivityFacultySignUpBinding signUpBinding;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserProfileChangeRequest userProfileChangeRequest;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private EditText emplyeeid;
    private static final String TAG = "SignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_sign_up);
        signUpBinding= DataBindingUtil.setContentView(FacultySignUp.this,R.layout.activity_faculty_sign_up);
        signUpBinding.setClickHandlers(new SignupactivityClickHandlers());
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        emplyeeid=findViewById(R.id.employeeid);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };

        getSupportActionBar().setTitle("Faculty Sign Up");
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        signUpButton=signUpBinding.signupbuttons;
        progressBar=signUpBinding.progressBar1;
        email=signUpBinding.emails;
        name=signUpBinding.name;
        department=signUpBinding.department;
        emplyeeid=signUpBinding.employeeid;
        password=signUpBinding.passwords;
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


    public class SignupactivityClickHandlers{
        public void onSignUpButtonClicked(View v)
        {
            if(email.getText().toString().trim().length()!=0&&name.getText().toString().trim().length()!=0&&department.getText().toString().trim().length()!=0&&password.getText().toString().trim().length()!=0&&emplyeeid.getText().toString().trim().length()!=0)
            {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FacultySignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userProfileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                            firebaseUser=firebaseAuth.getCurrentUser();
                            Faculty faculty=new Faculty();
                            faculty.setEmail(email.getText().toString().trim());
                            userID=firebaseUser.getUid();
                            faculty.setUuid(userID);
                            faculty.setEmployeeid(emplyeeid.getText().toString().trim());
                            faculty.setName(name.getText().toString().trim());
                            faculty.setDepartment(department.getText().toString().trim());
                            firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Hello", "User profile updated.");
                                    }                                }
                            });
                            myRef.child("faculties").child(firebaseUser.getUid()).setValue(faculty);
                            progressBar.setVisibility(View.GONE);
                            Intent i = new Intent(FacultySignUp.this, VerifyActivity.class);
                            i.putExtra("faculty",faculty);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                        }
                    }
                });
            } else {
                Toast.makeText(FacultySignUp.this, "Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

    }
}