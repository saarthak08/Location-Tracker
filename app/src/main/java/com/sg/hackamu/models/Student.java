package com.sg.hackamu.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class Student implements Parcelable {
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

    private String uuid;
    private String college;
    private String department;
    private String phoneno;
    private String enno;
    private String imageURI;


    public Student(String name, String password, long id, String email, boolean login, String uuid, String college, String department, String phoneno, String enno, String imageURI) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.login = login;
        this.imageURI=imageURI;
        this.uuid = uuid;
        this.college = college;
        this.department = department;
        this.phoneno = phoneno;
        this.enno = enno;
    }

    @Ignore
    public Student() {

    }


    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getEnno() {
        return enno;
    }

    public void setEnno(String enno) {
        this.enno = enno;
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

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.password);
        dest.writeLong(this.id);
        dest.writeString(this.email);
        dest.writeByte(this.login ? (byte) 1 : (byte) 0);
        dest.writeString(this.uuid);
        dest.writeString(this.college);
        dest.writeString(this.department);
        dest.writeString(this.phoneno);
        dest.writeString(this.enno);
        dest.writeString(this.imageURI);
    }

    protected Student(Parcel in) {
        this.name = in.readString();
        this.password = in.readString();
        this.id = in.readLong();
        this.email = in.readString();
        this.login = in.readByte() != 0;
        this.uuid = in.readString();
        this.college = in.readString();
        this.department = in.readString();
        this.phoneno = in.readString();
        this.enno = in.readString();
        this.imageURI = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}