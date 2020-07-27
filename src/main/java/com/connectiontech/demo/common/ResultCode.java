package com.connectiontech.demo.common;

/**
 * 请求返回状态
 */
public enum ResultCode {
    /**
     * ResultCode
     */
    SUCCESS(0),
    FAIL(1);
    private int code;
    private ResultCode(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
