package com.sg.hackamu.login;

import android.app.Application;

import com.sg.hackamu.login.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class DBViewModel extends AndroidViewModel {
    private LoginRepostory loginRepostory;
    public DBViewModel(@NonNull Application application) {
        super(application);
        loginRepostory=new LoginRepostory(application);
    }

    public void addUser(User user)
    {
        loginRepostory.AddUser(user);
    }

    public void deleteUser(User user)
    {
        loginRepostory.DeleteUser(user);
    }

    public void updateUser(long id)
    {
        loginRepostory.UpdateUser(id);
    }

    public List<User> getUsers()
    {
        return loginRepostory.getUsers();
    }

    public User getUser(long id)
    {
        return loginRepostory.getUser(id);
    }

    public User checkUser(String email)
    {
        return loginRepostory.checkUser(email);
    }

    public User checkUserPassword(String email,String password)
    {
        return loginRepostory.checkUserPassword(email,password);
    }

    public long getID(String email)
    {
        return loginRepostory.getID(email);
    }

    public User getLogin()
    {
        return loginRepostory.getLogin();
    }
}
