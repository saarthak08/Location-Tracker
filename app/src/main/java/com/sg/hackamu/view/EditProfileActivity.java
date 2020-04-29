package com.sg.hackamu.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sg.hackamu.R;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.view.students.StudentSignUp;

public class EditProfileActivity extends AppCompatActivity {
    private Student student;
    private Faculty faculty;
    private ScrollView scrollView;
    private boolean isuser;
    private TextView email;
    final static int PICK_IMAGE=2;
    private StorageReference mStorage;
    private String imageURI;
    final static int MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS=3;
    private EditText nameET;
    private EditText departmentET;
    private EditText collegeET;
    private EditText idET;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private Button saveButton;
    public ProgressBar progressBar;
    private Button cancelButton;
    private Uri selectedImageUri;
    private static final String TAG = "EditProfileActivity";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference=firebaseDatabase.getReference();
        mStorage= FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i = getIntent();
        progressBar=findViewById(R.id.progressBarTools);
        email=findViewById(R.id.emailTv);
        nameET=findViewById(R.id.nameET);
        saveButton=findViewById(R.id.saveButton);
        cancelButton=findViewById(R.id.cancelButton);
        imageView=findViewById(R.id.imageViewEP);
        departmentET=findViewById(R.id.departmentET);
        scrollView=findViewById(R.id.scrollViewEP);
        collegeET=findViewById(R.id.collegeET);
        idET=findViewById(R.id.idET);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        nameET.setText(firebaseUser.getDisplayName());
        if (i.hasExtra("student")) {
            student = i.getParcelableExtra("student");
            isuser=true;
            if(student.getEmail()==null) {
                email.setText("Phone Number: "+student.getPhoneno());
            }
            else{
            email.setText("Email:" +student.getEmail() );}
            departmentET.setText(student.getDepartment());
            collegeET.setText(student.getCollege());
            idET.setText(student.getEnno());
            if(student.getImageURI()!=null){
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(EditProfileActivity.this).load(student.getImageURI()).listener(requestListener()).into(imageView);
            }
        }
        if(student ==null)
        {
            faculty=i.getParcelableExtra("faculty");
            isuser=false;
            if(faculty.getEmail()==null){
                email.setText("Phone Number: "+faculty.getPhoneno());
            }
            else{
            email.setText("Email: "+faculty.getEmail());}
            departmentET.setText(faculty.getDepartment());
            collegeET.setText(faculty.getCollege());
            idET.setText(faculty.getEmployeeid());
            if(faculty.getImageURI()!=null){
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(EditProfileActivity.this).load(faculty.getImageURI()).listener(requestListener()).into(imageView);
            }
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setTitle("Uploading");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                UserProfileChangeRequest userProfileChangeRequest= new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameET.getText().toString()).build();
                firebaseUser.updateProfile(userProfileChangeRequest);
                if(isuser){
                    student.setCollege(collegeET.getText().toString().trim());
                    student.setName(nameET.getText().toString().trim());
                    student.setDepartment(departmentET.getText().toString().trim());
                    student.setEnno(idET.getText().toString().trim());
                    if(selectedImageUri!=null&&(!selectedImageUri.toString().equals(student.getImageURI()))) {
                        progressDialog.show();
                        final StorageReference filepath = mStorage.child("user_profile").child(firebaseUser.getUid());
                        StorageTask<UploadTask.TaskSnapshot> uploadTask=filepath.putFile(selectedImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                            }
                        });
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
                                    progressDialog.dismiss();
                                    databaseReference.child("students").child(firebaseUser.getUid()).setValue(student);
                                    Snackbar.make(findViewById(android.R.id.content),"Saved!",Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        databaseReference.child("students").child(firebaseUser.getUid()).setValue(student);
                        Snackbar.make(findViewById(android.R.id.content),"Saved!",Snackbar.LENGTH_SHORT).show();
                    }
                    ToolsActivity.student=student;
                }
                else{
                    faculty.setCollege(collegeET.getText().toString().trim());
                    faculty.setName(nameET.getText().toString().trim());
                    faculty.setDepartment(departmentET.getText().toString().trim());
                    faculty.setEmployeeid(idET.getText().toString().trim());
                    if(selectedImageUri!=null&& (!selectedImageUri.toString().equals(faculty.getImageURI()))) {
                        progressDialog.show();
                        final StorageReference filepath = mStorage.child("user_profile").child(firebaseUser.getUid());
                        StorageTask<UploadTask.TaskSnapshot> uploadTask=filepath.putFile(selectedImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                            }
                        });
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
                                    faculty.setImageURI(imageURI);
                                    progressDialog.dismiss();
                                    databaseReference.child("faculties").child(firebaseUser.getUid()).setValue(faculty);
                                    Snackbar.make(findViewById(android.R.id.content),"Saved!",Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        databaseReference.child("faculties").child(firebaseUser.getUid()).setValue(faculty);
                        Snackbar.make(findViewById(android.R.id.content),"Saved!",Snackbar.LENGTH_SHORT).show();
                    }
                    ToolsActivity.faculty=faculty;
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermissions();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                    Intent in=new Intent();
                    in.setType("image/*");
                    in.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(in, "Select Picture"), PICK_IMAGE);
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
            Glide.with(EditProfileActivity.this).load(selectedImageUri).into(imageView);

        }
    }

    public void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(EditProfileActivity.this).title("Permission Required")
                        .content("You need to give permission to select a profile picture")
                        .negativeText("Cancel")
                        .neutralText("Allow")
                        .positiveText("Go to Settings")
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent x = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivity(x);
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
            }
        }
        else {
            Intent in=new Intent();
            in.setType("image/*");
            in.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(in, "Select Picture"), PICK_IMAGE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public RequestListener<Drawable> requestListener(){
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        };
    }

}
