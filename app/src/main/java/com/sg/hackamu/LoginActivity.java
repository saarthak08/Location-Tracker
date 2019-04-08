package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sg.hackamu.databinding.ActivityLoginBinding;
import com.sg.hackamu.login.LoginRepostory;
import com.sg.hackamu.login.model.User;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding loginBinding;
    private Button signupButton;
    private Button loginButton;
    private EditText email;
    private EditText password;
    private TextView forgotpass;
    private LoginRepostory loginRepostory;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBinding= DataBindingUtil.setContentView(LoginActivity.this,R.layout.activity_login);
        loginBinding.setClickHandlers(new LoginActivityClickHandlers());
        loginRepostory=new LoginRepostory(getApplication());
        getSupportActionBar().setTitle("Login");
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
                if(loginRepostory.checkUser(user.getEmail()))
                {
                    if(loginRepostory.checkUserPassword(user.getEmail(),user.getPassword()))
                    {
                        User user2=new User();
                        user2.setId((int)loginRepostory.getID(user.getEmail()));
                        user2=loginRepostory.getUser(user2.getId());
                        user2.setLogin(true);
                        loginRepostory.UpdateUser(user2.getId());
                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
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

        }

        public void onForgotPasswordClicked(View view)
        {

        }

    }
}
