package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

import java.util.List;

public class getAllUsers extends AsyncTask<Void,Void, Void> {
    private UsersDAO usersDAO;
    private List<User> users;

    public getAllUsers(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        users=usersDAO.getUsers();
        return null;
    }

    public List<User> getUsers()
    {
        return users;
    }

}
