package com.example.hautc.chmessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hautc on 9/2/2017.
 */

public class Friend implements Serializable{
    String name;
    String lastestMessage;
    String lastestMessageDate;

    public Friend(String name, String lastestMessage, String lastestMessageDate) {
        this.name = name;
        this.lastestMessage = lastestMessage;
        this.lastestMessageDate = lastestMessageDate;
    }

    public Friend() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastestMessage() {
        return lastestMessage;
    }

    public void setLastestMessage(String lastestMessage) {
        this.lastestMessage = lastestMessage;
    }

    public String getLastestMessageDate() {
        return lastestMessageDate;
    }

    public void setLastestMessageDate(String lastestMessageDate) {
        this.lastestMessageDate = lastestMessageDate;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "name='" + name + '\'' +
                ", lastestMessage='" + lastestMessage + '\'' +
                ", lastestMessageDate='" + lastestMessageDate + '\'' +
                '}';
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name.replace('.',','));
        result.put("lastestMessage", lastestMessage);
        result.put("lastestMessageDate", lastestMessageDate);

        return  result;
    }
}
