package com.boyantomov.fetchdata;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 25.3.2015 г..
 */
public class User {
    String id;
    String userName;
    String avatarTemplate;

    public User(String id, String userName, String avatarTemplate){
        this.id = id;
        this.userName = userName;
        this.avatarTemplate = avatarTemplate;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarTemplate() {
        return avatarTemplate;
    }
    public void setAvatarTemplate(String avatarTemplate) {
        this.avatarTemplate = avatarTemplate;
    }
}
