package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sg.hackamu.databinding.ActivitySignUpBinding;
import com.sg.hackamu.login.DBViewModel;
import com.sg.hackamu.login.LoginRepostory;
import com.sg.hackamu.login.model.User;

public class SignUpActivity extends AppCompatActivity {
    private Button signUpButton;
    private EditText email;
    private EditText name;
    private EditText password;
    private DBViewModel dbViewModel;
    private ActivitySignUpBinding signUpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dbViewModel= ViewModelProviders.of(SignUpActivity.this).get(DBViewModel.class);
        signUpBinding=DataBindingUtil.setContentView(SignUpActivity.this,R.layout.activity_sign_up);
        signUpBinding.setClickHandlers(new SignupactivityClickHandlers());
        getSupportActionBar().setTitle("Student Sign Up");
        signUpButton=signUpBinding.signupbuttons;
        email=signUpBinding.emails;
        name=signUpBinding.name;
        password=signUpBinding.passwords;
    }

    public class SignupactivityClickHandlers{
        public void onSignUpButtonClicked(View v)
        {
            User user=new User();
            user.setEmail(email.getText().toString().trim());
            user.setName(name.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());
            if(email.getText().toString().trim().length()!=0&&name.getText().toString().trim().length()!=0&&password.getText().toString().trim().length()!=0)
            {
                if(dbViewModel.checkUser(user.getEmail())!=null) {
                    Toast.makeText(SignUpActivity.this,"Email is already registered",Toast.LENGTH_SHORT).show();
                }
                else {
                    user.setLogin(true);
                    dbViewModel.addUser(user);
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    SignUpActivity.this.finish();
                }
            }
            else
                Toast.makeText(SignUpActivity.this,"Invalid Empty Input",Toast.LENGTH_SHORT).show();
        }
    }
}
