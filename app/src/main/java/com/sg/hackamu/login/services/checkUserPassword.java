package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;

public class checkUserPassword extends AsyncTask<String,Void,Void> {
    String password;
    private UsersDAO usersDAO;
    private boolean check;

    public checkUserPassword(String password, UsersDAO usersDAO) {
        this.password = password;
        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(String... strings) {
        check=usersDAO.checkUserEmailPassword(strings[0],password);
        return null;
    }

    public boolean getcheckUserPassword()
    {
        return check;
    }
}
