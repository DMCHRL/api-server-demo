package com.connectiontech.demo.common;

/**
 * 自定义异常
 */
public class ClientException  extends RuntimeException {
    private int code;
    private String message;


    public ClientException(String msg) {
        this.code = ResultCode.FAIL.getCode();
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
