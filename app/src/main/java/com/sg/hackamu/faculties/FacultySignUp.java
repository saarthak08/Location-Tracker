package com.sg.hackamu.faculties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.authentication.SignupHandler;
import com.sg.hackamu.databinding.ActivityFacultySignUpBinding;
//import com.sg.hackamu.model.Faculty;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FacultySignUp extends AppCompatActivity {

    private Button signUpButton;
    private EditText email;
    public static EditText name;
    private EditText password;
    private EditText department;
    private EditText college;
    private EditText phonenumber;
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
    private boolean verification=false;
    private String uuid;
    private ImageView profilePicture;
    public static boolean verify;
    private boolean alreadyregister=false;
    private MaterialDialog dialog1;
    private String verificationCode;
    private String mVerificationId;
    private ScrollView scrollView;
    private StudentViewModel studentViewModel;
    private FacultyViewModel facultyViewModel;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String TAG = "StudentSignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_sign_up);
        signUpBinding= DataBindingUtil.setContentView(FacultySignUp.this,R.layout.activity_faculty_sign_up);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        emplyeeid=findViewById(R.id.employeeid);
        profilePicture=findViewById(R.id.imageViewProfilePictureFaculty);
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
        scrollView=signUpBinding.scrollView;
        phonenumber=signUpBinding.phoneNumber;
        college=signUpBinding.college;
        department=signUpBinding.department;
        emplyeeid=signUpBinding.employeeid;
        password=signUpBinding.passwords;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        facultyViewModel= ViewModelProviders.of(FacultySignUp.this).get(FacultyViewModel.class);
        studentViewModel=ViewModelProviders.of(FacultySignUp.this).get(StudentViewModel.class);
        signUpBinding.setClickHandlers(new SignupactivityClickHandlers(name.getText().toString().trim(),email.getText().toString().trim(),password.getText().toString().trim(),emplyeeid.getText().toString().trim(),phonenumber.getText().toString().trim(),FacultySignUp.this));
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


    public class SignupactivityClickHandlers extends SignupHandler {
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;
        public SignupactivityClickHandlers(String name, String email, String password, String emplyeeid, String phonenumber, Context context) {
            super(name, email, password, emplyeeid,phonenumber, context);
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
        }

        public void onSignUpButtonClicked(View v) {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            setContext(FacultySignUp.this);
            setEmail(email.getText().toString().trim());
            setPassword(password.getText().toString().trim());
            setName(name.getText().toString().trim());
            setNo(emplyeeid.getText().toString().trim());
            setPhonenumber(phonenumber.getText().toString().trim());
            checkInputs();
        }

        protected void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            progressBar.setVisibility(View.VISIBLE);
            scrollView.smoothScrollTo(progressBar.getScrollX(),progressBar.getScrollY());
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(FacultySignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                firebaseAuth=FirebaseAuth.getInstance();
                                firebaseUser=firebaseAuth.getCurrentUser();
                                uuid=firebaseUser.getUid();
                                studentViewModel.getAllStudents().observe(FacultySignUp.this, new Observer<List<DataSnapshot>>() {
                                    @Override
                                    public void onChanged(List<DataSnapshot> dataSnapshots) {
                                        for(DataSnapshot dataSnapshot:dataSnapshots){
                                            if (dataSnapshot.getKey().equals(uuid)) {
                                                alreadyregister=true;
                                            }
                                        }
                                    }
                                });
                               facultyViewModel.getAllFaculties().observe(FacultySignUp.this, new Observer<List<DataSnapshot>>() {
                                   @Override
                                   public void onChanged(List<DataSnapshot> dataSnapshots) {
                                       for(DataSnapshot dataSnapshot:dataSnapshots){
                                           if (dataSnapshot.getKey().equals(uuid)) {
                                               alreadyregister=true;
                                           }
                                       }
                                   }
                               });
                                createDialog3();
                                //verification successful we will start the profile activity
                            } else {
                                //verification unsuccessful.. display an error message
                                String message = "Error in verification!";
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    message = "Invalid code entered...";
                                }
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        protected void createUserwithEmail()
        {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.smoothScrollTo(progressBar.getScrollX(),progressBar.getScrollY());
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                Faculty faculty = new Faculty();
                                faculty.setEmail(email.getText().toString().trim());
                                userID = firebaseUser.getUid();
                                faculty.setUuid(userID);
                                faculty.setDepartment(department.getText().toString().trim());
                                faculty.setCollege(college.getText().toString().trim());
                                faculty.setEmployeeid(emplyeeid.getText().toString().trim());
                                faculty.setName(name.getText().toString().trim());
                                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Hello", "Student profile updated.");
                                        }
                                    }
                                });
                                progressBar.setVisibility(View.GONE);
                                facultyViewModel.addFaculty(faculty,firebaseUser.getUid());
                                Intent i = new Intent(FacultySignUp.this, VerifyActivity.class);
                                i.putExtra("faculty", faculty);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                //verification successful we will start the profile activity
                            } else {

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage().trim(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        protected void createDialog3() {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            new MaterialDialog.Builder(FacultySignUp.this)
                    .title("Checking Status....")
                    .positiveText("Proceed")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            verify=false;
                            if (alreadyregister) {

                                if (!dialog1.isCancelled()) {
                                    dialog1.dismiss();
                                    dialog1.cancel();
                                }
                                if (!dialog.isCancelled()) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                                Toast.makeText(getApplicationContext(), "Phone Number already registered.", Toast.LENGTH_SHORT).show();
                                alreadyregister=false;
                                firebaseAuth.signOut();
                            } else {
                                Faculty faculty = new Faculty();
                                userID = firebaseUser.getUid();
                                faculty.setUuid(userID);
                                faculty.setPhoneno(phonenumber.getText().toString().trim());
                                faculty.setDepartment(department.getText().toString().trim());
                                faculty.setCollege(college.getText().toString().trim());
                                faculty.setEmployeeid(emplyeeid.getText().toString().trim());
                                faculty.setName(name.getText().toString().trim());
                                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Hello", "Student profile updated."+firebaseUser.getDisplayName());
                                        }
                                    }
                                });
                                facultyViewModel.addFaculty(faculty,firebaseUser.getUid());
                                Intent i = new Intent(FacultySignUp.this, FacultyMainActivity.class);
                                i.putExtra("faculty", faculty);
                                verify=false;
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
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
    }

    @Override
    protected void onDestroy() {
        if(verify&&firebaseAuth.getCurrentUser()!=null)
        {
            firebaseAuth.getCurrentUser().delete();
            firebaseAuth.signOut();
        }
        super.onDestroy();
    }
}