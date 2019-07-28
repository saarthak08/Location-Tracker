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
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public abstract class LoginHandler {
    private String email;
    private String password;
    protected MaterialDialog dialog1;
    protected MaterialDialog dialog2;
    private String verificationCode;
    private String mVerificationId;
    private MaterialDialog loadingMaterialDialog;
    private DatabaseReference databaseReference;
    private String phonenumber;
    private FirebaseUser firebaseUser;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean login=false;
    public Context context;
    @Inject
    public FirebaseAuth firebaseAuth;


    public LoginHandler(String email, String password,Context context) {
        this.email = email;
        this.password = password;
        this.context=context;
        App.getApp().getComponent().inject(this);
        databaseReference= FirebaseUtils.getDatabase().getReference();
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

    protected boolean confirmEmailPasswordInput(){
        if(email.length()!=0&& password.length()!=0){
            return true;
        }
        else{
            Toast.makeText(context,"Error! Empty Inputs", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    protected void createDialogFirstForPhone(){
        dialog1 = new MaterialDialog.Builder(context).title("Enter your Phone Number with ISD code!")
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
                            createDialogSecondForPhone(phonenumber);

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



    private void createDialogSecondForPhone(String phoneno)
    {
        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                phoneno,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Activity) context,               // Activity (for callback binding)
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
        dialog2=new MaterialDialog.Builder(context).title("Enter the verification code you recieved!\nOTP not recieved? Try Again!\nSometimes, Google Play Services automatically verify your phone number without sending the code.")
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
            Toast toast = Toast.makeText(context, "Verification Code is wrong.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
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


    protected void hideLoadingMaterialDialogInstant(){
        if(loadingMaterialDialog!=null&&!loadingMaterialDialog.isCancelled()) {
            loadingMaterialDialog.dismiss();
            loadingMaterialDialog.cancel();
        }
    }
    protected abstract void createDialogThirdForPhone();

    protected abstract void signInWithPhoneAuthCredential(PhoneAuthCredential credential);

    protected abstract void checkInDatabaseAndLogin();

    protected abstract void checkInFacultyDatabase();

    protected abstract void checkInUserDatabase();
}
