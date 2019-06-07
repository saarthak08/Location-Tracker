package com.sg.hackamu.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.LauncherActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.faculties.FacultyLogin;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.User;
import com.sg.hackamu.students.LoginActivity;
import com.sg.hackamu.students.MainActivity;

public class VerifyActivity extends AppCompatActivity {
    TextView verifytext;
    Button ok;
    Button resend;
    Button Cancel;
    User user;
    boolean isuser;
    Faculty faculty;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase=FirebaseUtils.getDatabase();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        ok=findViewById(R.id.ok);
        databaseReference=firebaseDatabase.getReference();
        Intent i=getIntent();
        firebaseUser.sendEmailVerification();
        user=i.getParcelableExtra("student");
        isuser=true;
        if(user==null)
        {
            faculty=i.getParcelableExtra("faculty");
            isuser=false;
        }
        verifytext=findViewById(R.id.textverify);
        verifytext.setText("A verification link is sent to "+firebaseUser.getEmail()+". Please click on the link to verify it.\nAfter verifying, click on \'OK\' button to continue.");
        resend=findViewById(R.id.buttonresendverify);
        Cancel=findViewById(R.id.buttoncancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.reload();
                if(firebaseUser.isEmailVerified())
                {
                if(isuser)
                {
                    startActivity(new Intent(VerifyActivity.this, MainActivity.class));
                    VerifyActivity.this.finish();
                }
                else
                {
                    startActivity(new Intent(VerifyActivity.this, FacultyMainActivity.class));
                    VerifyActivity.this.finish();
                }
            }
            else{
                    Toast.makeText(VerifyActivity.this,"Error! Email isn't yet verified.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(firebaseUser.isEmailVerified()) {
                    Snackbar.make(v,"Email already Verified!",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Snackbar.make(v,"Verification Link sent!",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isuser)
                {
                    databaseReference.child("students").child(firebaseUser.getUid()).removeValue();
                    firebaseUser.delete();
                    startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
                    VerifyActivity.this.finish();
                }
                else {
                    databaseReference.child("faculties").child(firebaseUser.getUid()).removeValue();
                    firebaseUser.delete();
                    startActivity(new Intent(VerifyActivity.this, FacultyLogin.class));
                    VerifyActivity.this.finish();
                }
            }
        });
    }


}
