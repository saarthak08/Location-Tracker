package com.sg.hackamu.offlinelogin;

import android.app.Application;

import com.sg.hackamu.offlinelogin.db.UserDatabase;
import com.sg.hackamu.models.User;
import com.sg.hackamu.offlinelogin.services.AddUser;
import com.sg.hackamu.offlinelogin.services.DeleteUser;

import java.util.List;

public class LoginRepostory {
    public UsersDAO usersDAO;

    public LoginRepostory(Application application) {
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
        return usersDAO.getUsers();
    }

    public User getUser(long id)
    {
        return usersDAO.getUser(id);
    }

    public User checkUser(String email)
    {
        return usersDAO.checkUser(email);
    }
    public User checkUserPassword(String email, String password)
    {
        return usersDAO.checkUserEmailPassword(email,password);
    }

    public void UpdateUser(User user)
    {
        usersDAO.updateUser(user);
    }

    public long getID(String email)
    {
       return usersDAO.getID(email);
    }

    public User getLogin()
    {

        return usersDAO.getLogin(true);
    }
}
