package com.sg.hackamu.faculties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.LauncherActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivityFacultyLoginBinding;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.ForgotPassword;

public class FacultyLogin extends AppCompatActivity {
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private TextView forgotpass;
    private ImageView imageView2;
    private ScrollView scrollView;
    private ActivityFacultyLoginBinding loginBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private boolean login=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);

        loginBinding= DataBindingUtil.setContentView(FacultyLogin.this,R.layout.activity_faculty_login);
        loginBinding.setClickHandlers(new FacultyLoginActivityClickHandlers());
        getSupportActionBar().setTitle("Faculty Log In");
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        signupButton=loginBinding.signupbutton;
        progressBar=loginBinding.progressBar1;
        loginButton=loginBinding.loginButton;
        email=loginBinding.email;
        scrollView=loginBinding.scroll;
        imageView2=loginBinding.imageView2;
        databaseReference=firebaseDatabase.getReference();
        password=loginBinding.password;
        forgotpass=loginBinding.textViewforgotfac;
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
    public class FacultyLoginActivityClickHandlers{
        public void onLoginButtonClicked(View view) {
            if (email.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0) {
                progressBar.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(progressBar.getScrollX(),progressBar.getScrollY());
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FacultyLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth=FirebaseAuth.getInstance();
                            firebaseUser=firebaseAuth.getCurrentUser();
                            databaseReference.child("faculties").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds:dataSnapshot.getChildren())
                                    {
                                        if(ds.getKey().equals(firebaseUser.getUid()))
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Intent i = new Intent(FacultyLogin.this, FacultyMainActivity.class);
                                            startActivity(i);
                                            FacultyLogin.this.finish();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            databaseReference.child("students").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds:dataSnapshot.getChildren())
                                    {
                                        if(ds.getKey().equals(firebaseUser.getUid()))
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(FacultyLogin.this,"Error! Invalid Credentials",Toast.LENGTH_SHORT).show();
                                            firebaseAuth.signOut();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(FacultyLogin.this,"Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

        public void onSignUpButtonClicked (View view){
            startActivity(new Intent(FacultyLogin.this, FacultySignUp.class));

        }

        public void onForgotPasswordClicked (View view)
        {
            Intent t=new Intent(FacultyLogin.this, ForgotPassword.class);
            t.putExtra("isuser",false);
            startActivity(t);
        }
        public void onLoginAsFacultyClicked (View view)
        {
            startActivity(new Intent(FacultyLogin.this, LauncherActivity.class));
            FacultyLogin.this.finish();
        }

    }
}

