package com.example.hautc.chmessage;

/**
 * Created by hautc on 9/1/2017.
 */

public class UserInbox {
    String userName;
    String profileLoc;
    String latestMess;
    String date;

    public UserInbox() {
    }

    public UserInbox(String userName, String profileLoc, String latestMess, String date) {
        this.userName = userName;
        this.profileLoc = profileLoc;
        this.latestMess = latestMess;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileLoc() {
        return profileLoc;
    }

    public void setProfileLoc(String profileLoc) {
        this.profileLoc = profileLoc;
    }

    public String getLatestMess() {
        return latestMess;
    }

    public void setLatestMess(String latestMess) {
        this.latestMess = latestMess;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserInbox{" +
                "userName='" + userName + '\'' +
                ", profileLoc='" + profileLoc + '\'' +
                ", latestMess='" + latestMess + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
