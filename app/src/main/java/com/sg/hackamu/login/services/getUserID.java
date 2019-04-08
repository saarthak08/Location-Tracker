package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;

public class getUserID extends AsyncTask<String,Void,Void> {
    private UsersDAO usersDAO;
    private Long id;

    public getUserID(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(String... strings) {
        id=usersDAO.getID(strings[0]);
        return null;
    }

    public long getId()
    {
        return id;
    }
}
