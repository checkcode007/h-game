package com.z.model.common;

public class MsgResult {
    boolean ok;
    int code;
    String message;

    public MsgResult(boolean ok) {
        this.ok = ok;
    }

    public MsgResult(boolean ok, int code) {
        this.ok = ok;
        this.code = code;
    }
    public MsgResult(boolean ok, int code, String message) {
        this.ok = ok;
        this.code = code;
        this.message = message;
    }

    public void failMsg(String failMsg){
        this.ok = false;
        this.message = failMsg;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
