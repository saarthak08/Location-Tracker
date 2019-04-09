package com.sg.hackamu.offlinelogin.services;

import android.os.AsyncTask;

import com.sg.hackamu.offlinelogin.UsersDAO;
import com.sg.hackamu.offlinelogin.model.User;

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
