package com.sg.hackamu.view.faculties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.view.LauncherActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.utils.authentication.LoginHandler;
import com.sg.hackamu.databinding.ActivityFacultyLoginBinding;
import com.sg.hackamu.di.App;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.ForgotPassword;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import java.util.List;

import javax.inject.Inject;

public class FacultyLogin extends AppCompatActivity {

    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private MaterialDialog materialDialog;
    private TextView forgotpass;
    private boolean login;
    private ScrollView scrollView;
    private ActivityFacultyLoginBinding loginBinding;
    @Inject
    public FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private boolean verify;
    private boolean alreadyregister=false;
    private String uuid;
    private boolean isuser;
    private FacultyViewModel facultyViewModel;
    private StudentViewModel studentViewModel;
    private ImageView imageView;
    private  FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);
        loginBinding= DataBindingUtil.setContentView(FacultyLogin.this,R.layout.activity_faculty_login);
        getSupportActionBar().setTitle("Faculty Log In");
        App.getApp().getComponent().inject(this);
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        signupButton=loginBinding.signupbutton;
        imageView=loginBinding.imageView2;
        Glide.with(FacultyLogin.this).load(R.drawable.maps1).into(imageView);
        progressBar=loginBinding.progressBar1;
        loginButton=loginBinding.loginButton;
        scrollView=loginBinding.scrollView;
        email=loginBinding.email;
        databaseReference=firebaseDatabase.getReference();
        password=loginBinding.password;
        forgotpass=loginBinding.textViewforgotfac;
        studentViewModel =ViewModelProviders.of(FacultyLogin.this).get(StudentViewModel.class);
        facultyViewModel= ViewModelProviders.of(FacultyLogin.this).get(FacultyViewModel.class);
        loginBinding.setClickHandlers(new FacultyLoginActivityClickHandlers(email.getText().toString().trim(),password.getText().toString().trim(),FacultyLogin.this));

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
    public class FacultyLoginActivityClickHandlers extends LoginHandler{
        public FacultyLoginActivityClickHandlers(String email, String password, Context context) {
            super(email, password, context);
        }

        public void onLoginButtonClicked(View view) {
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


        public void onLoginViaPhone(View view)
        {
            progressBar.setVisibility(View.VISIBLE);
            createDialogFirstForPhone();
            progressBar.setVisibility(View.GONE);
        }

        protected void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            isuser=false;
            alreadyregister=false;
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(FacultyLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth=FirebaseAuth.getInstance();
                                firebaseUser=firebaseAuth.getCurrentUser();
                                uuid=firebaseUser.getUid();
                                verify=true;
                                facultyViewModel.getAllFaculties().observe(FacultyLogin.this, new Observer<List<DataSnapshot>>() {
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
                                studentViewModel.getAllStudents().observe(FacultyLogin.this, new Observer<List<DataSnapshot>>() {
                                    @Override
                                    public void onChanged(List<DataSnapshot> dataSnapshots) {
                                        for(DataSnapshot snapshot:dataSnapshots){
                                            if(snapshot.getKey().equals(uuid))
                                            {
                                                isuser=true;
                                            }
                                        }
                                    }
                                });
                                showLoadingDialogue();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingMaterialDialogInstant();
                                        createDialogThirdForPhone();
                                    }
                                },4000);

                            } else {
                                //verification unsuccessful.. display an error message
                            }
                        }
                    });
        }


        @Override
        protected void createDialogThirdForPhone()
        {
            login=true;
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            materialDialog=new MaterialDialog.Builder(FacultyLogin.this)
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
                                login=false;
                                startActivity(new Intent(FacultyLogin.this, FacultyMainActivity.class));
                                FacultyLogin.this.finish();
                            }
                            else
                            {
                                login=false;
                                Toast.makeText(getApplicationContext(),"Phone Number not registered or wrong type of login.",Toast.LENGTH_SHORT).show();
                                checkFirebaseAuthenticationForLoginViaPhone();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            login=false;
                            checkFirebaseAuthenticationForLoginViaPhone();
                            dialog.cancel();
                        }
                    })
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .autoDismiss(false)
                    .show();
        }

        private void checkFirebaseAuthenticationForLoginViaPhone(){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(firebaseAuth.getCurrentUser()!=null) {
                        if(!isuser){
                            firebaseUser=firebaseAuth.getCurrentUser();
                            try {
                                firebaseUser.delete();
                            }
                            catch (Exception e){
                                Log.d("LoginViaPhone",e.getMessage());
                            }
                        }
                        else {
                            firebaseAuth.signOut();
                        }
                        verify=false;
                    }
                }
            },5000);

        }

        @Override
        protected void checkInDatabaseAndLogin() {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            firebaseAuth.signInWithEmailAndPassword(getEmail(),getPassword()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FacultyLogin.this, e.getMessage().trim(), Toast.LENGTH_SHORT).show();
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
            facultyViewModel.getAllFaculties().observe(FacultyLogin.this, new Observer<List<DataSnapshot>>() {
                @Override
                public void onChanged(List<DataSnapshot> dataSnapshots) {
                    for(DataSnapshot snapshot:dataSnapshots){
                        if(snapshot.getKey().equals(firebaseUser.getUid()))
                        {
                            progressBar.setVisibility(View.GONE);
                            Intent i = new Intent(FacultyLogin.this, FacultyMainActivity.class);
                            startActivity(i);
                            FacultyLogin.this.finish();

                        }
                    }
                }
            });
        }

        @Override
        protected void checkInUserDatabase(){
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            studentViewModel.getAllStudents().observe(FacultyLogin.this, new Observer<List<DataSnapshot>>() {
                @Override
                public void onChanged(List<DataSnapshot> dataSnapshots) {
                    for(DataSnapshot snapshot:dataSnapshots){
                        if(snapshot.getKey().equals(firebaseUser.getUid()))
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(FacultyLogin.this,"Error! Invalid Credentials",Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        if(materialDialog!=null&&!materialDialog.isCancelled()){
            materialDialog.dismiss();
            materialDialog.cancel();
        }
        if(verify&&firebaseAuth.getCurrentUser()!=null)
        {
            firebaseAuth.signOut();
        }
        if(firebaseAuth.getCurrentUser()!=null&&login) {
            if(!isuser){
                firebaseUser=firebaseAuth.getCurrentUser();
                try {
                    firebaseUser.delete();
                }
                catch (Exception e){
                    Log.d("LoginViaPhone",e.getMessage());
                }
            }
            else {
                firebaseAuth.signOut();
            }
            verify=false;
        }
        super.onDestroy();
    }

}

