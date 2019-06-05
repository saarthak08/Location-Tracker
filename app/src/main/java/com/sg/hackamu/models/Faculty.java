package com.sg.hackamu.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Faculty implements Parcelable {

    private String name;
    private String department;
    private String college;
    private String password;
    private String email;
    private String uuid;
    private String employeeid;
    private boolean login;
    private String phoneno;
    private long id;

    public Faculty(String name, String department, String college, String password, String email, String uuid, String employeeid, boolean login, String phoneno, long id) {
        this.name = name;
        this.department = department;
        this.college = college;
        this.password = password;
        this.email = email;
        this.uuid = uuid;
        this.employeeid = employeeid;
        this.login = login;
        this.phoneno = phoneno;
        this.id = id;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public Faculty() {

    }

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.department);
        dest.writeString(this.college);
        dest.writeString(this.password);
        dest.writeString(this.email);
        dest.writeString(this.uuid);
        dest.writeString(this.employeeid);
        dest.writeByte(this.login ? (byte) 1 : (byte) 0);
        dest.writeString(this.phoneno);
        dest.writeLong(this.id);
    }

    protected Faculty(Parcel in) {
        this.name = in.readString();
        this.department = in.readString();
        this.college = in.readString();
        this.password = in.readString();
        this.email = in.readString();
        this.uuid = in.readString();
        this.employeeid = in.readString();
        this.login = in.readByte() != 0;
        this.phoneno = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<Faculty> CREATOR = new Creator<Faculty>() {
        @Override
        public Faculty createFromParcel(Parcel source) {
            return new Faculty(source);
        }

        @Override
        public Faculty[] newArray(int size) {
            return new Faculty[size];
        }
    };
}
