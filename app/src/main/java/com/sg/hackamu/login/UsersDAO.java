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
    public void addUser(User user);

    @Update
    public void updateUser(User user);

    @Delete
    public void deleteUser(User user);

    @Query("select * from user" )
    List<User> getUsers();

    @Query("select * from user where id==:userID")
    User getUser(long userID);

    @Query("select * from user where email=:userEmail")
    boolean checkUser(String userEmail);

    @Query("select * from user where password=:userPassword AND email=:userEmail ")
    boolean checkUserEmailPassword(String userEmail, String userPassword);

    @Query("select * from user where email=:email")
    long getID(String email);

}
