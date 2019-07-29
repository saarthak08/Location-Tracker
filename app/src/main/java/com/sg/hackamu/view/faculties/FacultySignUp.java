package com.sg.hackamu.view.faculties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sg.hackamu.R;
import com.sg.hackamu.utils.authentication.SignupHandler;
import com.sg.hackamu.databinding.ActivityFacultySignUpBinding;
//import com.sg.hackamu.model.Faculty;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;
import com.sg.hackamu.view.students.StudentSignUp;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import java.util.List;

public class FacultySignUp extends AppCompatActivity {

    private Button signUpButton;
    private EditText email;
    public static EditText name;
    private EditText password;
    private MaterialDialog materialDialog;
    private String imageURI;
    private StorageReference mStorage;
    private EditText department;
    private EditText college;
    private EditText phonenumber;
    private ImageView imageView;
    final static int PICK_IMAGE=2;
    final static int MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS=3;
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
    private Uri selectedImageUri;
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
    private static final String TAG = "FacultySignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_sign_up);
        signUpBinding= DataBindingUtil.setContentView(FacultySignUp.this,R.layout.activity_faculty_sign_up);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        emplyeeid=findViewById(R.id.employeeid);
        mStorage= FirebaseStorage.getInstance().getReference();
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
        imageView=signUpBinding.imageViewProfilePictureFaculty;
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
                               showLoadingDialogue();
                               new Handler().postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       hideLoadingMaterialDialogInstant();
                                       createDialog3();
                                   }
                               },4000);
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
                            Toast.makeText(getApplicationContext(),e.getMessage().trim(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        protected void createDialog3() {
            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            materialDialog=new MaterialDialog.Builder(FacultySignUp.this)
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
                                showLoadingDialogue();
                                firebaseUser = firebaseAuth.getCurrentUser();
                                updateImageAndStartActivity(1);

                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(firebaseAuth.getCurrentUser()!=null){
                                firebaseUser.delete();
                            }
                            progressBar.setVisibility(View.GONE);
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
            if(ContextCompat.checkSelfPermission(FacultySignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            Intent in=new Intent();
            in.setType("image/*");
            in.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(in, "Select Picture"), PICK_IMAGE);
        }

        @Override
        protected void updateImageAndStartActivity(int a){
            final Faculty faculty = new Faculty();
            final Intent i;
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
            if(a==0) {
               i = new Intent(FacultySignUp.this, VerifyActivity.class);
                faculty.setEmail(email.getText().toString().trim());
            }
            else{
                i = new Intent(FacultySignUp.this, FacultyMainActivity.class);
                faculty.setPhoneno(phonenumber.getText().toString().trim());
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if(selectedImageUri!=null) {
                final StorageReference filepath = mStorage.child("user_profile").child(firebaseUser.getUid());
                StorageTask<UploadTask.TaskSnapshot> uploadTask=filepath.putFile(selectedImageUri);
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
                            imageURI = downloadUri.toString();  faculty.setImageURI(imageURI);
                            faculty.setImageURI(imageURI);
                            facultyViewModel.addFaculty(faculty,firebaseUser.getUid());
                            progressBar.setVisibility(View.GONE);
                            i.putExtra("faculty", faculty);
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
            else {
                facultyViewModel.addFaculty(faculty, firebaseUser.getUid());
                i.putExtra("faculty", faculty);
                progressBar.setVisibility(View.GONE);
                startActivity(i);
                finish();
            }

        }
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
            Glide.with(FacultySignUp.this).load(selectedImageUri).into(imageView);

        }
    }

    public void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(FacultySignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FacultySignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(FacultySignUp.this).title("Permission Required")
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
                                ActivityCompat.requestPermissions(FacultySignUp.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(FacultySignUp.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
            }
            return;
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
            firebaseAuth.getCurrentUser().delete();
            firebaseAuth.signOut();
        }
        super.onDestroy();
    }
}