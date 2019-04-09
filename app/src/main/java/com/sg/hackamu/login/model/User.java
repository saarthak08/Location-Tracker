package com.sg.hackamu.login.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "email")
    private String email;


    @ColumnInfo(name = "login")
    private boolean login;

    public User(String name, String password, String email,long id,boolean login) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.login=login;
    }

    @Ignore
    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}