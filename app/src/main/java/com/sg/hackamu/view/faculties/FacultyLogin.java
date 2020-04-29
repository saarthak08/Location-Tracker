package com.sg.hackamu.view.faculties;

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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivityFacultyLoginBinding;
import com.sg.hackamu.di.App;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.ForgotPassword;
import com.sg.hackamu.utils.VerifyActivity;
import com.sg.hackamu.utils.authentication.LoginHandler;
import com.sg.hackamu.view.LauncherActivity;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;


public class FacultyLogin extends AppCompatActivity {

    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private MaterialDialog materialDialog;
    private boolean isVerified=false;
    private TextView forgotpass;
    private boolean login;
    private NestedScrollView scrollView;
    private ActivityFacultyLoginBinding loginBinding;
    @Inject
    public FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private List<DataSnapshot> allStudentsList = new ArrayList<>();
    private List<DataSnapshot> allFacultiesList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase = FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private boolean verify;
    private boolean alreadyRegister = false;
    private String uuid;
    private boolean isuser;
    private FacultyViewModel facultyViewModel;
    private StudentViewModel studentViewModel;
    private ImageView imageView;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);
        loginBinding = DataBindingUtil.setContentView(FacultyLogin.this, R.layout.activity_faculty_login);
        getSupportActionBar().setTitle("Faculty Log In");
        App.getApp().getComponent().inject(this);
        firebaseUser = firebaseAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                Log.d("Auth State", "Auth State Changed");

            }
        };
        signupButton = loginBinding.signupbutton;
        imageView = loginBinding.imageView2;
        Glide.with(FacultyLogin.this).load(R.drawable.maps1).into(imageView);
        progressBar = loginBinding.progressBar1;
        loginButton = loginBinding.loginButton;
        scrollView = loginBinding.scrollView;
        email = loginBinding.email;
        databaseReference = firebaseDatabase.getReference();
        password = loginBinding.password;
        forgotpass = loginBinding.textViewforgotfac;
        studentViewModel = ViewModelProviders.of(FacultyLogin.this).get(StudentViewModel.class);
        facultyViewModel = ViewModelProviders.of(FacultyLogin.this).get(FacultyViewModel.class);
        loginBinding.setClickHandlers(new FacultyLoginActivityClickHandlers(email.getText().toString().trim(), password.getText().toString().trim(), FacultyLogin.this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public class FacultyLoginActivityClickHandlers extends LoginHandler {
        public FacultyLoginActivityClickHandlers(String email, String password, Context context) {
            super(email, password, context);
        }

        public void onLoginButtonClicked(View view) {
            setEmail(email.getText().toString().trim());
            setPassword(password.getText().toString().trim());
            if (confirmEmailPasswordInput()) {
                progressBar.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(progressBar.getScrollX(), progressBar.getScrollY());
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                checkInDatabaseAndLogin();
            }

        }


        public void onSignUpButtonClicked(View view) {
            startActivity(new Intent(FacultyLogin.this, FacultySignUp.class));

        }


        public void onForgotPasswordClicked(View view) {
            Intent t = new Intent(FacultyLogin.this, ForgotPassword.class);
            t.putExtra("isuser", false);
            startActivity(t);
        }


        public void onLoginAsFacultyClicked(View view) {
            startActivity(new Intent(FacultyLogin.this, LauncherActivity.class));
            FacultyLogin.this.finish();
        }


        public void onLoginViaPhone(View view) {
            progressBar.setVisibility(View.VISIBLE);
            createDialogFirstForPhone();
            progressBar.setVisibility(View.GONE);
        }

        protected void signInWithPhoneAuthCredential(PhoneAuthCredential credential,String phoneNo) {
            showLoadingDialogue();
            alreadyRegister = false;
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            facultyViewModel.getAllInstantFacultiesList().thenAccept((List<DataSnapshot> list) -> {
                allFacultiesList = list;
                if (allFacultiesList.size() != 0) {
                    for (DataSnapshot d : allFacultiesList) {
                        if (d.getValue().equals(phoneNo)) {
                            alreadyRegister=true;
                        }
                    }
                } else {
                    Toast.makeText(FacultyLogin.this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                if(!alreadyRegister) {
                    Toast.makeText(FacultyLogin.this, "Error! User not registered.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(FacultyLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    hideLoadingMaterialDialogInstant();
                                    startActivity(new Intent(FacultyLogin.this, FacultyMainActivity.class));
                                    FacultyLogin.this.finish();
                                } else {
                                    String message = "Error in verification!";
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = "Invalid code entered...";
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    hideLoadingMaterialDialogInstant();
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });
        }


        @Override
        protected void checkInDatabaseAndLogin() {
            alreadyRegister=false;
            isVerified=false;
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            studentViewModel.getAllInstantStudentsList().thenAccept((List<DataSnapshot> list) -> {
                allStudentsList = list;
                if (allStudentsList.size() != 0) {
                    for (DataSnapshot d : allStudentsList) {
                        if (d.getValue().equals(email.getText().toString())) {
                            Toast.makeText(FacultyLogin.this, "Error! Invalid Credentials.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            hideLoadingMaterialDialogInstant();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(FacultyLogin.this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(getEmail(), getPassword()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FacultyLogin.this, e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser=firebaseAuth.getCurrentUser();
                            if(firebaseUser.isEmailVerified()) {
                                progressBar.setVisibility(View.GONE);
                                Intent i = new Intent(FacultyLogin.this, FacultyMainActivity.class);
                                startActivity(i);
                                FacultyLogin.this.finish();
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Intent i = new Intent(FacultyLogin.this, VerifyActivity.class);
                                Faculty faculty=new Faculty();
                                faculty.setEmail(email.getText().toString());
                                i.putExtra("faculty", faculty);
                                startActivity(i);
                                FacultyLogin.this.finish();
                            }
                        }
                    }
                });
            });
        }
    }
}

