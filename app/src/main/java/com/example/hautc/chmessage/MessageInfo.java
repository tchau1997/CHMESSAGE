package com.example.hautc.chmessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hautc on 9/5/2017.
 */

public class MessageInfo {
    String from;
    String to;
    String message;
    String time;

    @Override
    public String toString() {
        return "MessageInfo{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public MessageInfo(String from, String to, String message, String time) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.time = time;
    }

    public MessageInfo(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public MessageInfo() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("from", from);
        result.put("to", to);
        result.put("message", message);
        result.put("time", time);

        return result;
    }
}
