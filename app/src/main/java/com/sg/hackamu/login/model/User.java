package com.sg.hackamu.login.model;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "User")
public class User {
    private String name;
    private String password;
    private Integer id;
    private String email;


    public User(String name, String password, String email, Integer id) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}