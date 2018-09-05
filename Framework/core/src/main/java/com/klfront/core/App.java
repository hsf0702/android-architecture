package com.klfront.core;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class App extends BaseObject {
    @org.greenrobot.greendao.annotation.Id
    public int Id;
    @Property(nameInDb = "appId")
    public  String AppId ="";
    @Property(nameInDb = "Name")
    public String Name = "";
    @Property(nameInDb = "url")
    public String Url = "";
    public String getUrl() {
        return this.Url;
    }
    public void setUrl(String Url) {
        this.Url = Url;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getAppId() {
        return this.AppId;
    }
    public void setAppId(String AppId) {
        this.AppId = AppId;
    }
    public int getId() {
        return this.Id;
    }
    public void setId(int Id) {
        this.Id = Id;
    }
    @Generated(hash = 1741720358)
    public App(int Id, String AppId, String Name, String Url) {
        this.Id = Id;
        this.AppId = AppId;
        this.Name = Name;
        this.Url = Url;
    }
    @Generated(hash = 407064589)
    public App() {
    }
}

