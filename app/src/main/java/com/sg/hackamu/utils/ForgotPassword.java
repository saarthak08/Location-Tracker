package com.sg.hackamu.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.faculties.FacultyLogin;
import com.sg.hackamu.students.StudentLogin;

public class ForgotPassword extends AppCompatActivity {
    Button ok;
    Button Cancel;
    EditText email;
    boolean isuser;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase=FirebaseUtils.getDatabase();
    DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        Intent i=getIntent();
        isuser=i.getBooleanExtra("isuser",false);
        ok=findViewById(R.id.okforgotbutton);
        email=findViewById(R.id.emailsforgotpassword);
        databaseReference=firebaseDatabase.getReference();
        Cancel=findViewById(R.id.buttoncancelforgotpassword);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(email.getText().toString().trim().length() != 0) {
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    new MaterialDialog.Builder(ForgotPassword.this).title("Password reset link sent.\nIf you have recieved it, then tap on \'Exit\' & change your password via link.\nDidn't recieve it? Try Again!")
                                            .positiveText("Exit").negativeText("Try Again!").canceledOnTouchOutside(false).cancelable(false).positiveColor(getResources().getColor(R.color.colorPrimaryDark))
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if(isuser)
                                                    {
                                                        startActivity(new Intent(ForgotPassword.this, StudentLogin.class));
                                                        ForgotPassword.this.finish();
                                                    }
                                                    else {
                                                        startActivity(new Intent(ForgotPassword.this, FacultyLogin.class));
                                                        ForgotPassword.this.finish();
                                                    }

                                                }
                                            }).negativeColor(getResources().getColor(R.color.colorPrimaryDark)).onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            email.setText("");
                                            dialog.cancel();
                                        }
                                    }).show();
                                }
                                else
                                {
                                    Snackbar.make(v,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                                }
                        }
                    });
                }
                else
                {
                    Toast.makeText(ForgotPassword.this,"Error! Empty Input",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isuser)
                {
                    startActivity(new Intent(ForgotPassword.this, StudentLogin.class));
                    ForgotPassword.this.finish();
                }
                else {
                    startActivity(new Intent(ForgotPassword.this, FacultyLogin.class));
                    ForgotPassword.this.finish();
                }
            }
        });
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

}
