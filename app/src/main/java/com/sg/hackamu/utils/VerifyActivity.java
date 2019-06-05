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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    TextView verifytext;
    Button ok;
    Button resend;
    Button Cancel;
    User user;
    boolean isuser;
    Faculty faculty;
    FirebaseAuth firebaseAuth;
    String verificationCode;
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
        final Intent i=getIntent();
        firebaseUser.sendEmailVerification();
        user=i.getParcelableExtra("student");
        isuser=true;
        if(user==null)
        {
            faculty=i.getParcelableExtra("faculty");
            isuser=false;
        }
        verifytext=findViewById(R.id.textverify);
        verifytext.setText("A verification link is sent to "+firebaseUser.getEmail()+". Please click on the link to verify it.\nAfter verifying, tap on \'OK\' button to continue.\nTap on \'Cancel\' button to register again if you entered your credentials wrong.");
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
                /*    if(user.getPhoneno()!=0)
                    {
                        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                                user.getPhoneno(),        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                this,               // Activity (for callback binding)
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                                        // This callback will be invoked in two situations:
                                        // 1 - Instant verification. In some cases the phone number can be instantly
                                        //     verified without needing to send or enter a verification code.
                                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                        //     detect the incoming verification SMS and perform verification without
                                        //     user action.
                                        Log.d("PhoneVerify", "onVerificationCompleted:" + credential);

                                    }

                                    @Override
                                    public void onVerificationFailed(FirebaseException e) {

                                        Log.w("PhoneVerify", "onVerificationFailed", e);

                                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                            // Invalid request
                                            // ...
                                        } else if (e instanceof FirebaseTooManyRequestsException) {
                                            // The SMS quota for the project has been exceeded
                                            // ...
                                        }

                                        // Show a message and update the UI
                                        // ...
                                    }

                                    @Override
                                    public void onCodeSent(final String verificationId,
                                                           PhoneAuthProvider.ForceResendingToken token) {
                                        // The SMS verification code has been sent to the provided phone number, we
                                        // now need to ask the user to enter the code and then construct a credential
                                        // by combining the code with a verification ID.
                                        Log.d("Code Sent", "onCodeSent:" + verificationId);
                                        new MaterialDialog.Builder(getApplicationContext()).title("Verify your Phone Number. A one time password(O.T.P.) is sent to "+user.getPhoneno()+".\nEnter the OTP & Tap on \'OK\' button in 120 seconds.")
                                                .positiveText("OK")
                                                .negativeText("Cancel")
                                                .input("", "", false, new MaterialDialog.InputCallback() {
                                                    @Override
                                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                        verificationCode=input.toString().trim();
                                                    }
                                                })
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            PhoneAuthProvider.getCredential(verificationId,verificationCode);
                                                    }
                                                });
                                        // Save verification ID and resending token so we can use them later
                                        mVerificationId = verificationId;
                                        mResendToken = token;
                                        // ...
                                    }
                                });

                    }*/
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
