package com.sg.hackamu.students;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.LauncherActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.authentication.LoginHandler;
import com.sg.hackamu.databinding.ActivityLoginBinding;
import com.sg.hackamu.faculties.FacultyLogin;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.ForgotPassword;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import java.util.List;

public class StudentLogin extends AppCompatActivity {
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private TextView forgotpass;
    private ActivityLoginBinding loginBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase=FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private boolean alreadyregister=false;
    private String uuid;
    private boolean verify;
    private ScrollView scrollView;
    private StudentViewModel studentViewModel;
    private FacultyViewModel facultyViewModel;
    private  FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBinding= DataBindingUtil.setContentView(StudentLogin.this,R.layout.activity_login);
        getSupportActionBar().setTitle("Student Login");
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
        scrollView=loginBinding.scrollView;
        databaseReference=firebaseDatabase.getReference();
        password=loginBinding.password;
        forgotpass=loginBinding.textViewforgotstu;
        studentViewModel = ViewModelProviders.of(StudentLogin.this).get(StudentViewModel.class);
        facultyViewModel= ViewModelProviders.of(StudentLogin.this).get(FacultyViewModel.class);
        loginBinding.setClickHandlers(new LoginActivityClickHandlers(email.getText().toString().trim(),password.getText().toString().trim(), StudentLogin.this));

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

    public class LoginActivityClickHandlers extends LoginHandler {
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;
        public LoginActivityClickHandlers(String email, String password, Context context) {
            super(email, password, context);
        }

        public void onLoginButtonClicked(View view) {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            setEmail(email.getText().toString().trim());
            setPassword(password.getText().toString().trim());
            if(confirmEmailPasswordInput()){
                progressBar.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(progressBar.getScrollX(),progressBar.getScrollY());
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                checkInDatabaseAndLogin();
            }
        }

            public void onSignUpButtonClicked (View view){
                startActivity(new Intent(StudentLogin.this, StudentSignUp.class));

            }

            public void onForgotPasswordClicked (View view)
            {
                Intent t=new Intent(StudentLogin.this, ForgotPassword.class);
                t.putExtra("isuser",true);
                startActivity(t);
            }
            public void onLoginAsFacultyClicked (View view)
            {
                startActivity(new Intent(StudentLogin.this, LauncherActivity.class));
                StudentLogin.this.finish();
            }


        public void onLoginViaPhone(View view)
        {
            progressBar.setVisibility(View.VISIBLE);
            createDialogFirstForPhone();
            progressBar.setVisibility(View.GONE);
        }

        protected void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(StudentLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth=FirebaseAuth.getInstance();
                                firebaseUser=firebaseAuth.getCurrentUser();
                                uuid=firebaseUser.getUid();
                                verify=true;
                                studentViewModel.getAllStudents().observe(StudentLogin.this, new Observer<List<DataSnapshot>>() {
                                    @Override
                                    public void onChanged(List<DataSnapshot> dataSnapshots) {
                                        for(DataSnapshot snapshot:dataSnapshots){
                                            if(snapshot.getKey().equals(uuid))
                                            {
                                                alreadyregister=true;
                                            }
                                        }
                                    }
                                });
                                createDialogThirdForPhone();
                            } else {
                                //verification unsuccessful.. display an error message
                            }
                        }
                    });
        }


        @Override
        protected void createDialogThirdForPhone()
        {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            new MaterialDialog.Builder(StudentLogin.this)
                    .title("Checking Status....")
                    .positiveText("Proceed")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(alreadyregister)
                            {
                                if (!dialog1.isCancelled()) {
                                    dialog1.dismiss();
                                    dialog1.cancel();
                                }
                                if(!dialog2.isCancelled()){
                                    dialog2.cancel();
                                }
                                if(!dialog.isCancelled())
                                {
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                                progressBar.setVisibility(View.GONE);
                                verify=false;
                                startActivity(new Intent(StudentLogin.this, StudentMainActivity.class));
                                StudentLogin.this.finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Phone Number not registered or wrong type of login.",Toast.LENGTH_SHORT).show();
                                if(firebaseUser!=null) {
                                    firebaseAuth.signOut();
                                    verify=false;
                                }
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.cancel();
                        }
                    })
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .autoDismiss(false)
                    .show();
        }

        @Override
        protected void checkInDatabaseAndLogin() {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            firebaseAuth.signInWithEmailAndPassword(getEmail(),getPassword()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StudentLogin.this, e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkInFacultyDatabase();
                        checkInUserDatabase();

                    }
                }
            });
        }

        @Override
        protected void checkInFacultyDatabase(){
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            facultyViewModel.getAllFaculties().observe(StudentLogin.this, new Observer<List<DataSnapshot>>() {
                @Override
                public void onChanged(List<DataSnapshot> dataSnapshots) {
                    for(DataSnapshot snapshot:dataSnapshots){
                        if(snapshot.getKey().equals(firebaseUser.getUid()))
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(StudentLogin.this,"Error! Invalid Credentials",Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    }
                }
            });
        }

        @Override
        protected void checkInUserDatabase(){
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            studentViewModel.getAllStudents().observe(StudentLogin.this, new Observer<List<DataSnapshot>>() {
                @Override
                public void onChanged(List<DataSnapshot> dataSnapshots) {
                    for(DataSnapshot snapshot:dataSnapshots){
                        if(snapshot.getKey().equals(firebaseUser.getUid()))
                        {
                            progressBar.setVisibility(View.GONE);
                            Intent i = new Intent(StudentLogin.this, StudentMainActivity.class);
                            startActivity(i);
                            StudentLogin.this.finish();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if(verify&&firebaseAuth.getCurrentUser()!=null)
        {
            firebaseAuth.signOut();
        }
        super.onDestroy();
    }

}
