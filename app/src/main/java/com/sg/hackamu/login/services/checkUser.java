package com.sg.hackamu.login.services;

import android.os.AsyncTask;

import com.sg.hackamu.login.UsersDAO;

public class checkUser extends AsyncTask<String, Void, Void> {
    private UsersDAO usersDAO;
    private boolean check;

    public checkUser(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @Override
    protected Void doInBackground(String... strings) {
        check=usersDAO.checkUser(strings[0]);
        return null;
    }

    public boolean getcheckUser()
    {
        return check;
    }

}
