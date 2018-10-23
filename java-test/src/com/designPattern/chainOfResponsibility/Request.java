package com.designPattern.chainOfResponsibility;

/**
 * Request
 */
public class Request {
    private int level;
    private String msg;

    public Request(int level, String msg) {
        this.level = level;
        this.msg = msg;
    }

    public void setlevel(int level) {
        this.level = level;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getlevel() {
        return this.level;
    }

    public String getMsg() {
        return this.msg;
    }
}