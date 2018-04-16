package com.example.hautc.chmessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hautc on 9/1/2017.
 */

public class UserLogin {
    String userId;

    public UserLogin() {
    }

    public UserLogin(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "userId='" + userId + '\'' +
                '}';
    }

    public Map<String, Object> toMap(String path){
        HashMap<String, Object> result = new HashMap<>();
        result.put(path+userId, userId);

        return  result;
    }
}
