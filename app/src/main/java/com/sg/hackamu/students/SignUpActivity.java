package com.sg.hackamu.students;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivitySignUpBinding;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.models.User;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    private Button signUpButton;
    private EditText email;
    public static EditText name;
    private EditText password;
    private EditText department;
    private EditText college;
    private EditText phonenumber;
    private ActivitySignUpBinding signUpBinding;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserProfileChangeRequest userProfileChangeRequest;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private EditText enNo;
    String verificationCode;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String TAG = "SignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBinding = DataBindingUtil.setContentView(SignUpActivity.this, R.layout.activity_sign_up);
        signUpBinding.setClickHandlers(new SignupactivityClickHandlers());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                Log.d("Auth State", "Auth State Changed");

            }
        };

        getSupportActionBar().setTitle("Student Sign Up");
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        signUpButton = signUpBinding.signupbuttons;
        progressBar = signUpBinding.progressBar1;
        email = signUpBinding.emails;
        name = signUpBinding.name;
        department = signUpBinding.department;
        phonenumber = signUpBinding.phoneNumber;
        college = signUpBinding.college;
        enNo = signUpBinding.enrolno;
        password = signUpBinding.passwords;
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
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    public class SignupactivityClickHandlers {
        public void onSignUpButtonClicked(View v) {
            if (phonenumber.getText().toString().trim().length() != 0) {
                Toast.makeText(getApplicationContext(), "Error! Wrong Phone Number", Toast.LENGTH_SHORT).show();
            } else {
                signup();
            }
        }

        public void signup() {
            if (email.getText().toString().trim().length() != 0 && name.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0 && enNo.getText().toString().length() != 0) {
                progressBar.setVisibility(View.VISIBLE);
                verifyphone();

            } else {
                Toast.makeText(SignUpActivity.this, "Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

        public void verifyphone() {
            PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                    phonenumber.getText().toString().trim(),        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    SignUpActivity.this,               // Activity (for callback binding)
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            String code = credential.getSmsCode();
                            if (code != null) {
                                //verifying the code
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
            new MaterialDialog.Builder(SignUpActivity.this).title("Verify your Phone Number. A one time password(O.T.P.) is sent to " + phonenumber.getText() + ".\nEnter the OTP & Tap on \'OK\' button in 120 seconds.")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .input("", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            verificationCode = input.toString().trim();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                    .canceledOnTouchOutside(false).show();

        }

        private void verifyVerificationCode(String otp) {
            //creating the credential
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            //signing the user
            signInWithPhoneAuthCredential(credential);
        }

        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //verification successful we will start the profile activity
                                firebaseAuth.signOut();
                                createUser();

                            } else {
                                //verification unsuccessful.. display an error message
                                String message = "Error in verification!";
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    message = "Invalid code entered...";
                                }
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                                snackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                snackbar.show();
                            }
                        }
                    });
        }

        public void createUser() {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                        firebaseUser = firebaseAuth.getCurrentUser();
                        User user = new User();
                        user.setEmail(email.getText().toString().trim());
                        userID = firebaseUser.getUid();
                        user.setUuid(userID);
                        user.setPhoneno(Long.parseLong(phonenumber.getText().toString().trim()));
                        user.setDepartment(department.getText().toString().trim());
                        user.setCollege(college.getText().toString().trim());
                        user.setEnno(enNo.getText().toString().trim());
                        user.setName(name.getText().toString().trim());
                        firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Hello", "User profile updated.");
                                }
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        myRef.child("students").child(firebaseUser.getUid()).setValue(user);
                        Intent i = new Intent(SignUpActivity.this, VerifyActivity.class);
                        i.putExtra("student", user);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                    }
                }
            });
        }
    }
}

