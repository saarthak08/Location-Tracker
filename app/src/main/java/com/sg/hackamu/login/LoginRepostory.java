package com.sg.hackamu.login;

import android.app.Application;
import android.os.AsyncTask;

import com.sg.hackamu.login.db.UserDatabase;
import com.sg.hackamu.login.model.User;
import com.sg.hackamu.login.services.AddUser;
import com.sg.hackamu.login.services.DeleteUser;
import com.sg.hackamu.login.services.UpdateUser;
import com.sg.hackamu.login.services.checkUser;
import com.sg.hackamu.login.services.checkUserPassword;
import com.sg.hackamu.login.services.getAllUsers;
import com.sg.hackamu.login.services.getLogin;
import com.sg.hackamu.login.services.getUser;
import com.sg.hackamu.login.services.getUserID;

import java.util.List;

public class LoginRepostory {
    public UsersDAO usersDAO;
    public Application application;

    public LoginRepostory(Application application) {
        this.application = application;
        UserDatabase userDatabase=UserDatabase.getInstance(application);
        usersDAO=userDatabase.getUsersDAO();
    }

    public void AddUser(User user)
    {
        new AddUser(usersDAO).execute(user);
    }

    public void DeleteUser(User user)
    {
        new DeleteUser(usersDAO).execute(user);
    }

    public List<User> getUsers()
    {
        getAllUsers getAllUsers=new getAllUsers(usersDAO);
        return getAllUsers.getUsers();
    }

    public User getUser(long id)
    {
        getUser getUser = new getUser(usersDAO);
        getUser.execute(id);
        return getUser.getUser();
    }

    public boolean checkUser(String email)
    {
        checkUser checkUser=new checkUser(usersDAO);
        checkUser.execute(email);
        return checkUser.getcheckUser();
    }
    public boolean checkUserPassword(String email, String password)
    {
        checkUserPassword checkUserPassword=new checkUserPassword(password,usersDAO);
        checkUserPassword.execute(email);
        return checkUserPassword.getcheckUserPassword();
    }

    public void UpdateUser(long id)
    {
        User user=getUser(id);
        new UpdateUser(usersDAO).execute(user);
    }

    public long getID(String email)
    {
        getUserID getUserID=new getUserID(usersDAO);
        getUserID.execute(email);
        return getUserID.getId();
    }

    public User getLogin()
    {
        User user;
        getLogin getLogin= new getLogin(usersDAO);
        return user=getLogin.getLoginUser();
    }
}
