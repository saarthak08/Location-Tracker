package com.sg.hackamu.faculties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.sg.hackamu.databinding.ActivityFacultySignUpBinding;
//import com.sg.hackamu.model.Faculty;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.User;
import com.sg.hackamu.students.MainActivity;
import com.sg.hackamu.students.SignUpActivity;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;

import java.util.concurrent.Executor;
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
    private boolean verify;
    private boolean alreadyregister=false;
    private MaterialDialog dialog1;
    private String verificationCode;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
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
        phonenumber=signUpBinding.phoneNumber;
        college=signUpBinding.college;
        department=signUpBinding.department;
        emplyeeid=signUpBinding.employeeid;
        password=signUpBinding.passwords;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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
        public void onSignUpButtonClicked(View v) {
            if (name.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0 && emplyeeid.getText().toString().length() != 0) {
                if (email.getText().toString().trim().length() != 0 && phonenumber.getText().toString().trim().length() != 0) {
                    Toast.makeText(getApplicationContext(),"Enter either Email or Phone Number.",Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().trim().length() == 0 && phonenumber.getText().toString().trim().length() != 0) {
                    verify=true;
                    verifyphone();

                } else if (email.getText().toString().trim().length() != 0 && phonenumber.getText().toString().trim().length() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    createUserwithEmail();
                }
            }
            else {
                Toast.makeText(FacultySignUp.this, "Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

        public void verifyphone () {
            progressBar.setVisibility(View.VISIBLE);
            PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                    phonenumber.getText().toString().trim(),        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    FacultySignUp.this,               // Activity (for callback binding)
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            final String code = credential.getSmsCode();
                            if (code != null) {
                                //verifying the code
                                if(!dialog1.isCancelled())
                                {
                                    dialog1.getInputEditText().setText(code);
                                    dialog1.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            verifyVerificationCode(code);
                                        }
                                    });
                                    dialog1.getBuilder().positiveFocus(true);
                                }
                                verifyVerificationCode(code);
                            }
                            Log.d("PhoneVerify", "onVerificationCompleted:" + credential);

                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Log.w("PhoneVerify", "onVerificationFailed", e);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(final String verificationId,
                                               PhoneAuthProvider.ForceResendingToken token) {
                            Log.d("Code Sent", "onCodeSent:" + verificationId);
                            mVerificationId = verificationId;
                            mResendToken = token;
                            // ...
                        }
                    });
            dialog1 = new MaterialDialog.Builder(FacultySignUp.this).title("Verify your Phone Number. A one time password (O.T.P.) is sent to " + phonenumber.getText() + ".\nEnter the OTP & Tap on \'OK\' button in 120 seconds.")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            verificationCode = input.toString().trim();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                verificationCode = dialog.getInputEditText().getText().toString().trim();
                            } catch (Exception e) {
                                Log.d("verification", e.getMessage());
                            }
                            verifyVerificationCode(verificationCode);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    }).cancelable(false)
                    .canceledOnTouchOutside(false).autoDismiss(false).show();

        }

        private void verifyVerificationCode(String otp){
            //creating the credential
            progressBar.setVisibility(View.GONE);
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                signInWithPhoneAuthCredential(credential);
            } catch (Exception e) {
                Toast toast = Toast.makeText(FacultySignUp.this, "Verification Code is wrong", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(FacultySignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                firebaseAuth=FirebaseAuth.getInstance();
                                firebaseUser=firebaseAuth.getCurrentUser();
                                uuid=firebaseUser.getUid();
                                myRef.child("students").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        try {

                                            if (dataSnapshot.getKey().equals(uuid)) {
                                                alreadyregister=true;
                                            }
                                        }
                                        catch(Exception e)
                                        {
                                            Log.d("LoginPN", e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                myRef.child("faculties").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        try {

                                            if (dataSnapshot.getKey().equals(uuid)) {
                                                alreadyregister=true;
                                            }
                                        }
                                        catch(Exception e)
                                        {
                                            Log.d("LoginPN", e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                createdialog3();

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

        public void createUserwithEmail()
        {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                Faculty user = new Faculty();
                                user.setEmail(email.getText().toString().trim());
                                userID = firebaseUser.getUid();
                                user.setUuid(userID);
                                user.setDepartment(department.getText().toString().trim());
                                user.setCollege(college.getText().toString().trim());
                                user.setEmployeeid(emplyeeid.getText().toString().trim());
                                user.setName(name.getText().toString().trim());
                                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Hello", "User profile updated.");
                                        }
                                    }
                                });
                                progressBar.setVisibility(View.GONE);
                                myRef.child("faculties").child(firebaseUser.getUid()).setValue(user);
                                Intent i = new Intent(FacultySignUp.this, VerifyActivity.class);
                                i.putExtra("faculty", user);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
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
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        public void createdialog3() {
            new MaterialDialog.Builder(FacultySignUp.this)
                    .title("Checking Status....")
                    .positiveText("Proceed")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                                Faculty user = new Faculty();
                                userID = firebaseUser.getUid();
                                user.setUuid(userID);
                                user.setPhoneno(phonenumber.getText().toString().trim());
                                user.setDepartment(department.getText().toString().trim());
                                user.setCollege(college.getText().toString().trim());
                                user.setEmployeeid(emplyeeid.getText().toString().trim());
                                user.setName(name.getText().toString().trim());
                                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Hello", "User profile updated."+firebaseUser.getDisplayName());
                                        }
                                    }
                                });
                                myRef.child("faculties").child(firebaseUser.getUid()).setValue(user);

                                Intent i = new Intent(FacultySignUp.this, FacultyMainActivity.class);
                                i.putExtra("faculty", user);
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