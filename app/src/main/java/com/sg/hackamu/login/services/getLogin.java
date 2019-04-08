package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

public class getLogin extends AsyncTask<Void,Void,Void> {
    User user;
    private UsersDAO usersDAO;

    public getLogin(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        user=usersDAO.getLogin(true);
        return null;
    }

    public User getLoginUser()
    {
        return user;
    }
}
