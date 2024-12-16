package com.z.model.common;

public class MsgResult<T> {
    boolean ok;
    int code;
    String message;
    T t;

    public MsgResult(T t) {
        this.ok = true;
        this.t = t;
    }

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
    public MsgResult(String failMsg) {
        this.ok = false;
        this.message = failMsg;
    }
    public void failMsg(String failMsg){
        this.ok = false;
        this.message = failMsg;
    }
    public void ok(T t){
        this.ok = true;
        this.t = t;
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

    public T getT() {
        return t;
    }
}
