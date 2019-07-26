package com.sg.hackamu.students;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivitySignUpBinding;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;

import java.util.concurrent.TimeUnit;

public class StudentSignUp extends AppCompatActivity {
    private Button signUpButton;
    private EditText email;
    private EditText name;
    private EditText password;
    private EditText department;
    private EditText college;
    Uri selectedImageUri;
    private EditText phonenumber;
    private ImageView imageView;
    final static int PICK_IMAGE=2;
    final static int MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS=3;
    private ActivitySignUpBinding signUpBinding;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserProfileChangeRequest userProfileChangeRequest;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private String uuid;
    private Uri imageURI;
    private ScrollView scrollView;
    private EditText enNo;
    private boolean verify;
    private StorageReference mStorage;
    private boolean alreadyregister=false;
    private MaterialDialog dialog1;
    private String verificationCode;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String TAG = "StudentSignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBinding = DataBindingUtil.setContentView(StudentSignUp.this, R.layout.activity_sign_up);
        signUpBinding.setClickHandlers(new SignupactivityClickHandlers());
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        mStorage=FirebaseStorage.getInstance().getReference();
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
        imageView=signUpBinding.imageViewProfilePictureStudent;
        department = signUpBinding.department;
        scrollView=signUpBinding.scrollView;
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
                if (email.getText().toString().trim().length() != 0 && phonenumber.getText().toString().trim().length() != 0) {
                    Toast.makeText(getApplicationContext(),"Enter either Email or Phone Number.",Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().trim().length() == 0 && phonenumber.getText().toString().trim().length() != 0) {
                    verify=true;
                    verifyphone();

                } else if (email.getText().toString().trim().length() != 0 && phonenumber.getText().toString().trim().length() == 0) {
                    createUserwithEmail();
                }
            }
            else {
                Toast.makeText(StudentSignUp.this, "Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

            public void verifyphone () {
                progressBar.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(progressBar.getScrollX(),progressBar.getScrollY());
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                        phonenumber.getText().toString().trim(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        StudentSignUp.this,               // Activity (for callback binding)
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
                                }
                                progressBar.setVisibility(View.GONE);
                                signInWithPhoneAuthCredential(credential);
                                Log.d("PhoneVerify", "onVerificationCompleted:" + credential);

                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Log.w("PhoneVerify", "onVerificationFailed", e);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(final String verificationId,
                                                   PhoneAuthProvider.ForceResendingToken token) {
                                Log.d("Code Sent", "onCodeSent:" + verificationId);
                                mVerificationId = verificationId;
                                mResendToken = token;
                                progressBar.setVisibility(View.GONE);
                                // ...
                            }
                        });
                dialog1 = new MaterialDialog.Builder(StudentSignUp.this).title("Verify your Phone Number. A one time password (O.T.P.) is sent to " + phonenumber.getText() + ".\nEnter the OTP & Tap on \'OK\' button in 120 seconds.\nOTP not recieved? Try Again!\nSometimes, Google Play Services can automatically verify your phone number without sending the code.")
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
                    Toast toast = Toast.makeText(StudentSignUp.this, "Verification Code is wrong", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(StudentSignUp.this, new OnCompleteListener<AuthResult>() {
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
                                    Student student = new Student();
                                    student.setEmail(email.getText().toString().trim());
                                    userID = firebaseUser.getUid();
                                    student.setUuid(userID);
                                    if(selectedImageUri!=null) {
                                        StorageReference filepath = mStorage.child("user_profile").child(selectedImageUri.getLastPathSegment());
                                        filepath.putFile(selectedImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                if (progress == 100) {

                                                    //upload();
                                                }
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageURI = taskSnapshot.getUploadSessionUri();
                                            }
                                        });
                                    }
                                    if(imageURI!=null) {
                                        userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(imageURI).build();
                                        firebaseUser.updateProfile(userProfileChangeRequest);
                                    }
                                    student.setDepartment(department.getText().toString().trim());
                                    student.setCollege(college.getText().toString().trim());
                                    student.setEnno(enNo.getText().toString().trim());
                                    student.setName(name.getText().toString().trim());
                                    userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                                    firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Hello", "Student profile updated.");
                                            }
                                        }
                                    });
                                    myRef.child("students").child(firebaseUser.getUid()).setValue(student);
                                    Intent i = new Intent(StudentSignUp.this, VerifyActivity.class);
                                    i.putExtra("student", student);
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
        public void createdialog3() {
            new MaterialDialog.Builder(StudentSignUp.this)
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
                                final Student student = new Student();
                                userID = firebaseUser.getUid();
                                student.setUuid(userID);
                                student.setPhoneno(phonenumber.getText().toString().trim());
                                student.setDepartment(department.getText().toString().trim());
                                student.setCollege(college.getText().toString().trim());
                                student.setEnno(enNo.getText().toString().trim());
                                student.setName(name.getText().toString().trim());
                                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString().trim()).build();
                                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Hello", "Student profile updated."+firebaseUser.getDisplayName());
                                        }
                                    }
                                });
                                myRef.child("students").child(firebaseUser.getUid()).setValue(student);
                                if(selectedImageUri!=null) {
                                    StorageReference filepath = mStorage.child("user_profile").child(selectedImageUri.getLastPathSegment());
                                    filepath.putFile(selectedImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            if (progress == 100) {
                                                //upload();
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imageURI = taskSnapshot.getUploadSessionUri();
                                            if(imageURI!=null) {
                                                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(imageURI).build();
                                                firebaseUser.updateProfile(userProfileChangeRequest);
                                            }

                                            Intent i = new Intent(StudentSignUp.this, StudentMainActivity.class);
                                            i.putExtra("student", student);
                                            verify=false;
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);

                                        }
                                    });
                                }
                                else {
                                    Intent i = new Intent(StudentSignUp.this, StudentMainActivity.class);
                                    i.putExtra("student", student);
                                    verify = false;
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
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

        public void onImageClicked(View v)
        {
            requestStoragePermissions();
            if(ContextCompat.checkSelfPermission(StudentSignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            Intent in=new Intent();
            in.setType("image/*");
            in.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(in, "Select Picture"), PICK_IMAGE);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Permission Denied");
                }

        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data.getData();
            imageView.setPadding(4,4,4,4);
            Glide.with(StudentSignUp.this).load(selectedImageUri).into(imageView);

        }
    }

    public void requestStoragePermissions()
    {
        if(ContextCompat.checkSelfPermission(StudentSignUp.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(StudentSignUp.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                new MaterialDialog.Builder(StudentSignUp.this).title("Permission Required")
                        .content("You need to give permission to select a profile picture")
                        .negativeText("Cancel")
                        .neutralText("Allow")
                        .positiveText("Go to Settings")
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent x= new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                finish();
                                startActivity(x);
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ActivityCompat.requestPermissions(StudentSignUp.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
                            }
                        })
                        .show();
            }
            else
            {
                ActivityCompat.requestPermissions(StudentSignUp.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
            }
            return;
        }
    }

}

