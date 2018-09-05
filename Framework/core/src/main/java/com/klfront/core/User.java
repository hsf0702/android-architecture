package com.klfront.core;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class User extends BaseObject {
    @Id
    public int Id;
    @Property(nameInDb = "loginId")
    public  String LoginId ="";
    @Property(nameInDb = "Nickname")
    public String Nickname = "";
    @Property(nameInDb = "email")
    public String Email = "";
    public boolean Activied = true;
    public boolean getActivied() {
        return this.Activied;
    }
    public void setActivied(boolean Activied) {
        this.Activied = Activied;
    }
    public String getEmail() {
        return this.Email;
    }
    public void setEmail(String Email) {
        this.Email = Email;
    }
    public String getNickname() {
        return this.Nickname;
    }
    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }
    public String getLoginId() {
        return this.LoginId;
    }
    public void setLoginId(String LoginId) {
        this.LoginId = LoginId;
    }
    public int getId() {
        return this.Id;
    }
    public void setId(int Id) {
        this.Id = Id;
    }
    @Generated(hash = 733860817)
    public User(int Id, String LoginId, String Nickname, String Email,
            boolean Activied) {
        this.Id = Id;
        this.LoginId = LoginId;
        this.Nickname = Nickname;
        this.Email = Email;
        this.Activied = Activied;
    }
    @Generated(hash = 586692638)
    public User() {
    }
}
