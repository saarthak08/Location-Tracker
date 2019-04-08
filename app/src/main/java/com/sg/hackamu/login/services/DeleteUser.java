package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

public class DeleteUser extends AsyncTask<User,Void,Void> {
    public UsersDAO usersDAO;

    public DeleteUser(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }


    @Override
    protected Void doInBackground(User... users) {
        usersDAO.deleteUser(users[0]);
        return null;
    }
}
