package com.sg.hackamu.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sg.hackamu.R;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.view.faculties.FacultyLogin;
import com.sg.hackamu.view.students.StudentLogin;

public class ToolsActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private boolean isuser;
    public static Student student;
    public static Faculty faculty;
    private TextView editProfile;
    private StorageReference mStorage;
    private TextView deleteAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        editProfile=findViewById(R.id.textViewEditProfile);
        deleteAccount=findViewById(R.id.textViewDeleteAccount);
        databaseReference=firebaseDatabase.getReference();
        mStorage=FirebaseStorage.getInstance().getReference();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i = getIntent();
        if (i.hasExtra("student")) {
            student = i.getParcelableExtra("student");
            isuser=true;
        }
        if(student ==null)
        {
            faculty=i.getParcelableExtra("faculty");
            isuser=false;
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            Intent i=new Intent(ToolsActivity.this,EditProfileActivity.class);
            @Override
            public void onClick(View v) {
                if(isuser){
                    i.putExtra("student",student);
                    startActivity(i);
                }
                else{
                    i.putExtra("faculty",faculty);
                    startActivity(i);
                }
            }
        });
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ToolsActivity.this).title("Delete Account!")
                        .content("Are you sure you want to delete your account?")
                        .positiveText("Proceed")
                        .positiveColor(getResources().getColor(R.color.colorPrimaryDark))
                        .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                        .negativeText("Cancel")
                        .canceledOnTouchOutside(true)
                        .cancelable(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if(isuser){
                                    Intent i=new Intent(ToolsActivity.this, StudentLogin.class);
                                    databaseReference.child("students").child(firebaseUser.getUid()).removeValue();
                                    mStorage.child("user_profile").child(firebaseUser.getUid()).delete();
                                    firebaseAuth=FirebaseAuth.getInstance();
                                    firebaseUser=firebaseAuth.getCurrentUser();
                                    firebaseUser.delete();
                                    firebaseAuth.signOut();
                                    startActivity(i);
                                    finish();
                                }
                                else{
                                    Intent i=new Intent(ToolsActivity.this, FacultyLogin.class);
                                    databaseReference.child("faculties").child(firebaseUser.getUid()).removeValue();
                                    mStorage.child("user_profile").child(firebaseUser.getUid()).delete();
                                    firebaseAuth=FirebaseAuth.getInstance();
                                    firebaseUser=firebaseAuth.getCurrentUser();
                                    firebaseUser.delete();
                                    firebaseAuth.signOut();
                                    startActivity(i);
                                    finish();
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                dialog.cancel();
                            }
                        }).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
