package com.sg.hackamu.faculties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.LauncherActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.ActivityFacultyLoginBinding;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.students.LoginActivity;
import com.sg.hackamu.students.MainActivity;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.ForgotPassword;

import java.util.concurrent.TimeUnit;

public class FacultyLogin extends AppCompatActivity {
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private TextView forgotpass;
    private ScrollView scrollView;
    private ActivityFacultyLoginBinding loginBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    private DatabaseReference databaseReference;
    private MaterialDialog dialog1;
    private MaterialDialog dialog2;
    private boolean verify;
    private String verificationCode;
    private boolean alreadyregister=false;
    private String uuid;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String phonenumber;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private boolean login=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);

        loginBinding= DataBindingUtil.setContentView(FacultyLogin.this,R.layout.activity_faculty_login);
        loginBinding.setClickHandlers(new FacultyLoginActivityClickHandlers());
        getSupportActionBar().setTitle("Faculty Log In");
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        signupButton=loginBinding.signupbutton;
        progressBar=loginBinding.progressBar1;
        loginButton=loginBinding.loginButton;
        scrollView=loginBinding.scrollView;
        email=loginBinding.email;
        databaseReference=firebaseDatabase.getReference();
        password=loginBinding.password;
        forgotpass=loginBinding.textViewforgotfac;
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
    public class FacultyLoginActivityClickHandlers{
        public void onLoginButtonClicked(View view) {
            if (email.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0) {
                progressBar.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(progressBar.getScrollX(),progressBar.getScrollY());
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FacultyLogin.this, e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth=FirebaseAuth.getInstance();
                            firebaseUser=firebaseAuth.getCurrentUser();
                            databaseReference.child("faculties").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds:dataSnapshot.getChildren())
                                    {
                                        if(ds.getKey().equals(firebaseUser.getUid()))
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Intent i = new Intent(FacultyLogin.this, FacultyMainActivity.class);
                                            startActivity(i);
                                            FacultyLogin.this.finish();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            databaseReference.child("students").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds:dataSnapshot.getChildren())
                                    {
                                        if(ds.getKey().equals(firebaseUser.getUid()))
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(FacultyLogin.this,"Error! Invalid Credentials",Toast.LENGTH_SHORT).show();
                                            firebaseAuth.signOut();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(FacultyLogin.this,"Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            }
        }

        public void onSignUpButtonClicked (View view){
            startActivity(new Intent(FacultyLogin.this, FacultySignUp.class));

        }

        public void onForgotPasswordClicked (View view)
        {
            Intent t=new Intent(FacultyLogin.this, ForgotPassword.class);
            t.putExtra("isuser",false);
            startActivity(t);
        }
        public void onLoginAsFacultyClicked (View view)
        {
            startActivity(new Intent(FacultyLogin.this, LauncherActivity.class));
            FacultyLogin.this.finish();
        }

        public void onLoginViaPhone(View view)
        {
            dialog1 = new MaterialDialog.Builder(FacultyLogin.this).title("Enter your Phone Number!")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .inputType(InputType.TYPE_CLASS_PHONE)
                    .input("", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            phonenumber=dialog1.getInputEditText().getText().toString().trim();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                phonenumber=dialog1.getInputEditText().getText().toString().trim();
                                createdialog2(phonenumber);

                            } catch (Exception e) {
                                Log.d("verification", e.getMessage().trim());
                            }
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
        private void createdialog2(String phoneno)
        {
            PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                    phoneno,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    FacultyLogin.this,               // Activity (for callback binding)
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            final String code = credential.getSmsCode();
                            if (code != null) {
                                //verifying the code
                                if(!dialog2.isCancelled())
                                {
                                    dialog2.getInputEditText().setText(code);
                                    dialog2.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            verifyVerificationCode(code);
                                        }
                                    });
                                    dialog2.getBuilder().positiveFocus(true);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            signInWithPhoneAuthCredential(credential);
                            Log.d("PhoneVerify", "onVerificationCompleted:" + credential);

                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Log.w("PhoneVerify", "onVerificationFailed", e);
                            Toast.makeText(getApplicationContext(), e.getMessage().trim(), Toast.LENGTH_SHORT).show();
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
            dialog2=new MaterialDialog.Builder(FacultyLogin.this).title("Enter the verification code you recieved!\nOTP not recieved? Try Again!\nSometimes, Google Play Services automatically verify your phone number without sending the code.")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input(null, null, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            verificationCode=input.toString().trim();
                        }
                    }).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                verificationCode=dialog2.getInputEditText().getText().toString().trim();
                                verifyVerificationCode(verificationCode);
                            }
                            catch (Exception e)
                            {
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    }).canceledOnTouchOutside(false)
                    .autoDismiss(false)
                    .cancelable(false).show();
        }

        private void verifyVerificationCode(String otp){
            //creating the credential
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                signInWithPhoneAuthCredential(credential);
            } catch (Exception e) {
                Toast toast = Toast.makeText(FacultyLogin.this, "Verification Code is wrong.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(FacultyLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth=FirebaseAuth.getInstance();
                                firebaseUser=firebaseAuth.getCurrentUser();
                                uuid=firebaseUser.getUid();
                                verify=true;
                                databaseReference.child("faculties").addChildEventListener(new ChildEventListener() {
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

                            } else {
                                //verification unsuccessful.. display an error message
                            }
                        }
                    });
        }
        public void createdialog3()
        {
            new MaterialDialog.Builder(FacultyLogin.this)
                    .title("Checking Status....")
                    .positiveText("Proceed")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(alreadyregister)
                            {

                                if (!dialog1.isCancelled()) {
                                    dialog1.dismiss();
                                    dialog1.cancel();
                                }
                                if(!dialog2.isCancelled()){
                                    dialog2.cancel();
                                }
                                if(!dialog.isCancelled())
                                {
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                                verify=false;
                                startActivity(new Intent(FacultyLogin.this, FacultyMainActivity.class));
                                FacultyLogin.this.finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Phone Number not registered or wrong type of login.",Toast.LENGTH_SHORT).show();
                                if(firebaseUser!=null) {
                                    firebaseUser.delete();
                                    firebaseAuth.signOut();
                                    verify=false;
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

