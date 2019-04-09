package com.sg.hackamu.login.db;

import android.content.Context;

import com.sg.hackamu.login.UsersDAO;
import com.sg.hackamu.login.model.User;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class}, version=2)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UsersDAO getUsersDAO();
    private static UserDatabase instance;
    public  static  synchronized UserDatabase getInstance(Context context)
    {
        if(instance ==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),UserDatabase.class,"hackamu").allowMainThreadQueries().addCallback(callback).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback callback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
