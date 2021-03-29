package com.qyl.server.utils;

/**
 * @Author: qyl
 * @Date: 2020/12/7 9:54
 */
public enum ResponseEnum {
    SUCCESS(1, "成功"),
    FAIL(0, "失败"),

    ILLEGAL_ARGS(1001, "参数不合法"),
    NULL_ARGS(1002, "参数为空"),
    ;

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态信息
     */
    private String msg;

    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
