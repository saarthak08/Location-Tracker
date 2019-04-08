package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

public class AddUser extends AsyncTask<User,Void,Void> {
    private UsersDAO usersDAO;

    public AddUser(UsersDAO usersDAO) {

        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(User... users) {
        usersDAO.addUser(users[0]);
        return null;
    }
}
