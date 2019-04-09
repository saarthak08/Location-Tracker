package com.sg.hackamu.login;


import com.sg.hackamu.login.model.User;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsersDAO {

    @Insert
    void addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("select * from user" )
    List<User> getUsers();

    @Query("select * from user where id==:userID")
    User getUser(long userID);

    @Query("select * from user where email==:userEmail")
    User checkUser(String userEmail);

    @Query("select * from user where email ==:userEmail AND user.password ==:userPassword ")
    User checkUserEmailPassword(String userEmail, String userPassword);

    @Query("select id from user where email==:email")
    long getID(String email);

    @Query("select * from user where login==:login")
    User getLogin(boolean login);

}
