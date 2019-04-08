package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

public class getUser extends AsyncTask<Long,Void,Void> {
    private UsersDAO usersDAO;
    private User user;

    public getUser(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        user=usersDAO.getUser(longs[0]);
        return null;
    }

    public User getUser()
    {
        return user;
    }
}
