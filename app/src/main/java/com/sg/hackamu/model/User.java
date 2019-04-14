package com.sg.hackamu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class User implements Parcelable {
    private String name;

    private String password;


    private long id;

    private String email;


    private boolean login;

    private String uuid;

    private String FacultyNo;

    private String EnNo;

    public User(String name, String password, String email,long id,boolean login,String uuid) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.login=login;
        this.uuid=uuid;
    }

    public String getFacultyNo() {
        return FacultyNo;
    }

    public void setFacultyNo(String facultyNo) {
        FacultyNo = facultyNo;
    }

    public String getEnNo() {
        return EnNo;
    }

    public void setEnNo(String enNo) {
        EnNo = enNo;
    }

    public User() {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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


    protected User(Parcel in) {
        name = in.readString();
        password = in.readString();
        id = in.readLong();
        email = in.readString();
        login = in.readByte() != 0x00;
        uuid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeLong(id);
        dest.writeString(email);
        dest.writeByte((byte) (login ? 0x01 : 0x00));
        dest.writeString(uuid);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}