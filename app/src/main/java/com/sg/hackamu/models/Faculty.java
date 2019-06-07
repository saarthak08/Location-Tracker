package com.sg.hackamu.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Faculty implements Parcelable {
    private String name;

    private String department;

    private String password;

    private long id;

    private String email;


    private boolean login;

    private String uuid;

    private String employeeid;

    public Faculty(String name, String department, String password, String email, long id, boolean login, String uuid, String employeeid) {
        this.name = name;
        this.department=department;
        this.password = password;
        this.id = id;
        this.email = email;
        this.login=login;
        this.uuid=uuid;
        this.employeeid=employeeid;
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

    protected Faculty(Parcel in) {
        name = in.readString();
        department=in.readString();
        password = in.readString();
        id = in.readLong();
        employeeid=in.readString();
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
        dest.writeString(department);
        dest.writeString(password);
        dest.writeLong(id);
        dest.writeString(employeeid);
        dest.writeString(email);
        dest.writeByte((byte) (login ? 0x01 : 0x00));
        dest.writeString(uuid);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Faculty> CREATOR = new Parcelable.Creator<Faculty>() {
        @Override
        public Faculty createFromParcel(Parcel in) {
            return new Faculty(in);
        }

        @Override
        public Faculty[] newArray(int size) {
            return new Faculty[size];
        }
    };
}
