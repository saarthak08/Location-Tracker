package com.sg.hackamu.students;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
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
    private EditText name;
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
    private boolean verify;
    private MaterialDialog dialog;
    private String verificationCode;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String TAG = "SignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBinding = DataBindingUtil.setContentView(SignUpActivity.this, R.layout.activity_sign_up);
        signUpBinding.setClickHandlers(new SignupactivityClickHandlers());
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
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
            if (name.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0 && enNo.getText().toString().length() != 0) {
                progressBar.setVisibility(View.VISIBLE);
                if (email.getText().toString().trim().length() != 0 && phonenumber.getText().toString().trim().length() != 0) {
                    verify=true;
                    verifyphone();
                } else if (email.getText().toString().trim().length() == 0 && phonenumber.getText().toString().trim().length() != 0) {
                    verify=false;
                    verifyphone();

                } else if (email.getText().toString().trim().length() != 0 && phonenumber.getText().toString().trim().length() == 0) {
                    createUserwithEmail();
                }
            }
            else {
                Toast.makeText(SignUpActivity.this, "Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

            public void verifyphone () {
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
                dialog = new MaterialDialog.Builder(SignUpActivity.this).title("Verify your Phone Number. A one time password(O.T.P.) is sent to " + phonenumber.getText() + ".\nEnter the OTP & Tap on \'OK\' button in 120 seconds.")
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
                try {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                } catch (Exception e) {
                    Toast toast = Toast.makeText(SignUpActivity.this, "Verification Code is wrong", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (!dialog.isCancelled()) {
                                        dialog.dismiss();
                                        dialog.cancel();
                                    }
                                    firebaseUser = firebaseAuth.getCurrentUser();
                                    User user = new User();
                                    user.setEmail(email.getText().toString().trim());
                                    userID = firebaseUser.getUid();
                                    user.setUuid(userID);
                                    user.setPhoneno(phonenumber.getText().toString().trim());
                                    user.setDepartment(department.getText().toString().trim());
                                    user.setCollege(college.getText().toString().trim());
                                    user.setEnno(enNo.getText().toString().trim());
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
                                    progressBar.setVisibility(View.GONE);
                                    myRef.child("students").child(firebaseUser.getUid()).setValue(user);
                                    if(verify) {
                                        Intent i = new Intent(SignUpActivity.this, VerifyActivity.class);
                                        i.putExtra("student", user);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                    else{
                                        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                                        i.putExtra("student", user);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
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
                                    User user = new User();
                                    user.setEmail(email.getText().toString().trim());
                                    userID = firebaseUser.getUid();
                                    user.setUuid(userID);
                                    user.setPhoneno(phonenumber.getText().toString().trim());
                                    user.setDepartment(department.getText().toString().trim());
                                    user.setCollege(college.getText().toString().trim());
                                    user.setEnno(enNo.getText().toString().trim());
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
                                    myRef.child("students").child(firebaseUser.getUid()).setValue(user);
                                    Intent i = new Intent(SignUpActivity.this, VerifyActivity.class);
                                    i.putExtra("student", user);
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

