package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

public class UpdateUser extends AsyncTask<User,Void,Void> {
    private UsersDAO usersDAO;

    public UpdateUser(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }


    @Override
    protected Void doInBackground(User... users) {
        usersDAO.updateUser(users[0]);
        return null;
    }
}
