package com.sg.hackamu.utils.authentication;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.sg.hackamu.R;
import com.sg.hackamu.di.App;
import com.sg.hackamu.view.faculties.FacultySignUp;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


public abstract class SignupHandler {
    private String email;
    private String password;
    private String no;
    private String name;
    protected MaterialDialog dialog1;
    protected MaterialDialog dialog2;
    private String verificationCode;
    private String mVerificationId;
    private DatabaseReference databaseReference;
    private String phonenumber;
    private FirebaseUser firebaseUser;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean login=false;
    private MaterialDialog loadingMaterialDialog;
    private Context context;
    @Inject
    public FirebaseAuth firebaseAuth;

    public SignupHandler(String name, String email, String password,String no,String phonenumber,Context context) {
        this.email = email;
        this.password = password;
        this.no=no;
        this.name=name;
        this.context=context;
        this.phonenumber=phonenumber;
        App.getApp().getComponent().inject(this);
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseUtils.getDatabase().getReference();
    }

    protected void checkInputs(){
        if (name.length()!=0 && password.length() != 0 && no.length() != 0) {
            if (email.length()!=0 && phonenumber.length() != 0) {
                Toast.makeText(context,"Enter either Email or Phone Number.",Toast.LENGTH_SHORT).show();
            } else if (email.length() == 0 && phonenumber.length() != 0) {
                FacultySignUp.verify=true;
                verifyphone();

            } else if (email.length() != 0 && phonenumber.length() == 0) {
                createUserwithEmail();
            }
        }
        else {
            Toast.makeText(context, "Error! Empty Inputs", Toast.LENGTH_SHORT).show();
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    protected void verifyphone () {
        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                phonenumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Activity) context,               // Activity (for callback binding)
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
                        signInWithPhoneAuthCredential(credential);
                        Log.d("PhoneVerify", "onVerificationCompleted:" + credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.w("PhoneVerify", "onVerificationFailed", e);
                        Toast.makeText(context, e.getMessage().trim(), Toast.LENGTH_SHORT).show();
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
        dialog1 = new MaterialDialog.Builder(context).title("Verify your Phone Number. A one time password (O.T.P.) is sent to " + phonenumber+ ".\nEnter the OTP & Tap on \'OK\' button in 120 seconds.\nOTP not recieved? Try Again!\nSometimes, Google Play Services can automatically verify your phone number without sending the code.")
                .positiveText("OK")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        verificationCode = input.toString().trim();
                    }
                })
                .positiveColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .negativeColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {
                            verificationCode = dialog.getInputEditText().getText().toString().trim();
                        } catch (Exception e) {
                            Log.d("verification", e.getMessage().trim());
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

    protected void showLoadingDialogue(){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.loading_dialogue, null);
        loadingMaterialDialog=new MaterialDialog.Builder(context).customView(promptsView,true)
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    protected void verifyVerificationCode(String otp){
        //creating the credential
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Toast toast = Toast.makeText(context, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    protected void hideLoadingMaterialDialogInstant(){
        if(loadingMaterialDialog!=null&&!loadingMaterialDialog.isCancelled()) {
            loadingMaterialDialog.dismiss();
            loadingMaterialDialog.cancel();
        }
    }

    protected abstract void updateImageAndStartActivity(int a);

    protected abstract void signInWithPhoneAuthCredential(PhoneAuthCredential credential);

    protected abstract void createDialog3();

    protected abstract void createUserwithEmail();
}
