package com.sg.hackamu.view.students;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivitySignUpBinding;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;
import com.sg.hackamu.utils.authentication.SignupHandler;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class StudentSignUp extends AppCompatActivity {
    private Button signUpButton;
    private EditText email;
    private EditText name;
    private EditText password;
    private EditText department;
    private EditText college;
    private Uri selectedImageUri;
    private EditText phonenumber;
    private MaterialDialog materialDialog;
    private ImageView imageView;
    final static int PICK_IMAGE = 2;
    final static int MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS = 3;
    private ActivitySignUpBinding signUpBinding;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private List<DataSnapshot> allStudentsList = new ArrayList<>();
    private List<DataSnapshot> allFacultiesList = new ArrayList<>();
    private UserProfileChangeRequest userProfileChangeRequest;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private String uuid;
    private String imageURI;
    private ScrollView scrollView;
    private EditText enNo;
    private StorageReference mStorage;
    private FacultyViewModel facultyViewModel;
    private StudentViewModel studentViewModel;
    private static final String TAG = "StudentSignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBinding = DataBindingUtil.setContentView(StudentSignUp.this, R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
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
        imageView = signUpBinding.imageViewProfilePictureStudent;
        department = signUpBinding.department;
        scrollView = signUpBinding.scrollView;
        phonenumber = signUpBinding.phoneNumber;
        college = signUpBinding.college;
        enNo = signUpBinding.enrolno;
        password = signUpBinding.passwords;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        facultyViewModel = ViewModelProviders.of(StudentSignUp.this).get(FacultyViewModel.class);
        studentViewModel = ViewModelProviders.of(StudentSignUp.this).get(StudentViewModel.class);
        signUpBinding.setClickHandlers(new StudentSignUp.SignUpActivityClickHandlers(name.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString().trim(), enNo.getText().toString().trim(), phonenumber.getText().toString().trim(), StudentSignUp.this));

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


    public class SignUpActivityClickHandlers extends SignupHandler {
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;

        public SignUpActivityClickHandlers(String name, String email, String password, String no, String phonenumber, Context context) {
            super(name, email, password, no, phonenumber, context);
        }

        public void onSignUpButtonClicked(View v) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            setContext(StudentSignUp.this);
            setEmail(email.getText().toString().trim());
            setPassword(password.getText().toString().trim());
            setName(name.getText().toString().trim());
            setNo(enNo.getText().toString().trim());
            setPhonenumber(phonenumber.getText().toString().trim());
            checkInputs();
        }

        protected void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            showLoadingDialogue();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.smoothScrollTo(progressBar.getScrollX(), progressBar.getScrollY());
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            facultyViewModel.getAllInstantFacultiesList().thenAccept((List<DataSnapshot> list) -> {
                allFacultiesList = list;
                if (allFacultiesList.size() != 0) {
                    for (DataSnapshot d : allFacultiesList) {
                        if (d.getValue().equals(phonenumber.getText().toString())) {
                            Toast.makeText(StudentSignUp.this, "User already exists", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            hideLoadingMaterialDialogInstant();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(StudentSignUp.this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    hideLoadingMaterialDialogInstant();
                    return;
                }
                studentViewModel.getAllInstantStudentsList().thenAccept((List<DataSnapshot> stuList) -> {
                    allStudentsList = stuList;
                    if (allStudentsList.size() != 0) {
                        for (DataSnapshot d : allStudentsList) {
                            if (d.getValue().equals(phonenumber.getText().toString())) {
                                Toast.makeText(StudentSignUp.this, "User already exists", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                hideLoadingMaterialDialogInstant();
                                return;

                            }
                        }
                    } else {
                        Toast.makeText(StudentSignUp.this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        hideLoadingMaterialDialogInstant();
                        return;
                    }
                    firebaseAuth.signInWithCredential(credential)
                            .addOnCompleteListener(StudentSignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        updateImageAndStartActivity(1);
                                    } else {
                                        //verification unsuccessful.. display an error message
                                        String message = "Error in verification!";
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            message = "Invalid code entered...";
                                        }
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        hideLoadingMaterialDialogInstant();
                                    }
                                }
                            });
                });
            });
        }


        protected void createUserWithEmail() {
            progressBar.setVisibility(View.VISIBLE);
            scrollView.smoothScrollTo(progressBar.getScrollX(), progressBar.getScrollY());
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                showLoadingDialogue();
                                updateImageAndStartActivity(0);
                                //verification successful we will start the profile activity
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                            hideLoadingMaterialDialogInstant();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }


        public void onImageClicked(View v) {
            requestStoragePermissions();
        }

        @Override
        protected void updateImageAndStartActivity(int a) {
            final Intent i;
            final Student student = new Student();
            try {
                firebaseUser=firebaseAuth.getCurrentUser();
                userID = firebaseUser.getUid();
                student.setUuid(userID);
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
                if (a == 0) {
                    i = new Intent(StudentSignUp.this, VerifyActivity.class);
                    student.setEmail(email.getText().toString().trim());
                    studentViewModel.addStudentToFacultiesList(student.getEmail(),firebaseUser.getUid());
                } else {
                    i = new Intent(StudentSignUp.this, StudentMainActivity.class);
                    student.setPhoneno(phonenumber.getText().toString().trim());
                    studentViewModel.addStudentToFacultiesList(student.getPhoneno(),firebaseUser.getUid());
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if (selectedImageUri != null) {
                    final StorageReference filepath = mStorage.child("user_profile").child(firebaseUser.getUid());
                    StorageTask<UploadTask.TaskSnapshot> uploadTask = filepath.putFile(selectedImageUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }// Continue with the task to get the download URL
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                imageURI = downloadUri.toString();
                                student.setImageURI(imageURI);
                                hideLoadingMaterialDialogInstant();
                                studentViewModel.addStudent(student, firebaseUser.getUid());
                                i.putExtra("student", student);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                } else {
                    i.putExtra("student", student);
                    studentViewModel.addStudent(student, firebaseUser.getUid());
                    hideLoadingMaterialDialogInstant();
                    progressBar.setVisibility(View.GONE);
                    startActivity(i);
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(StudentSignUp.this,"An error occurred. Please try again!",Toast.LENGTH_SHORT).show();
                Log.d("Hello",e.getMessage());
                hideLoadingMaterialDialogInstant();
                progressBar.setVisibility(View.GONE);
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent();
                    in.setType("image/*");
                    in.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(in, "Select Picture"), PICK_IMAGE);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Permission Denied");
                }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data.getData();
            imageView.setPadding(4, 4, 4, 4);
            Glide.with(StudentSignUp.this).load(selectedImageUri).into(imageView);

        }
    }

    public void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(StudentSignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(StudentSignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(StudentSignUp.this).title("Permission Required")
                        .content("You need to give permission to select a profile picture")
                        .negativeText("Cancel")
                        .neutralText("Allow")
                        .positiveText("Go to Settings")
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent x = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                finish();
                                startActivity(x);
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ActivityCompat.requestPermissions(StudentSignUp.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(StudentSignUp.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
            }
        }
        else {
            Intent in = new Intent();
            in.setType("image/*");
            in.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(in, "Select Picture"), PICK_IMAGE);
        }
    }

}

