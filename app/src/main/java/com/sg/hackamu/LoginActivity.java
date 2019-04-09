package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sg.hackamu.databinding.ActivityLoginBinding;
import com.sg.hackamu.login.DBViewModel;
import com.sg.hackamu.login.LoginRepostory;
import com.sg.hackamu.login.model.User;

public class LoginActivity extends AppCompatActivity {
    private Button signupButton;
    private Button loginButton;
    private EditText email;
    private EditText password;
    private TextView forgotpass;
    private User user;
    private DBViewModel dbViewModel;
    private ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbViewModel= ViewModelProviders.of(LoginActivity.this).get(DBViewModel.class);
        loginBinding= DataBindingUtil.setContentView(LoginActivity.this,R.layout.activity_login);
        loginBinding.setClickHandlers(new LoginActivityClickHandlers());
        getSupportActionBar().setTitle("Student Login");
        signupButton=loginBinding.signupbutton;
        loginButton=loginBinding.loginButton;
        email=loginBinding.email;
        password=loginBinding.password;
    }
    public class LoginActivityClickHandlers{
        public void onLoginButtonClicked(View view){
            if(email.getText().toString().trim().length()!=0&&password.getText().toString().trim().length()!=0)
            {
                user=new User();
                user.setEmail(email.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                if(dbViewModel.checkUser(user.getEmail())!=null)
                {
                    if(dbViewModel.checkUserPassword(user.getEmail(),user.getPassword())!=null)
                    {
                        User user2;
                        user2=dbViewModel.getUser(dbViewModel.getID(user.getEmail()));
                        user2.setLogin(true);
                        dbViewModel.updateUser(user2);
                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        LoginActivity.this.finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"No User Found",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Snackbar.make(view, "Email or Password is empty", Snackbar.LENGTH_SHORT).show();
            }

        }

        public void onSignUpButtonClicked(View view){
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));

        }

        public void onForgotPasswordClicked(View view)
        {

        }
        public void onLoginAsFacultyClicked(View view)
        {
            startActivity(new Intent(LoginActivity.this,LauncherActivity.class));
            LoginActivity.this.finish();
        }

    }

}
