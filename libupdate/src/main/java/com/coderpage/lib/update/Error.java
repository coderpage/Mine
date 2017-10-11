package com.coderpage.lib.update;

/**
 * @author lc. 2017-09-23 23:49
 * @since 0.5.0
 */

public class Error {
    private int code;
    private String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "ERR: code=" + code() + " message=" + message();
    }
}
