package com.qyl.server.exception;

/**
 * @Author: qyl
 * @Date: 2021/3/29 16:06
 */
public class TicketServerException extends RuntimeException {

    /**
     * 异常码
     */
    private int code;

    /**
     * 异常信息
     */
    private String msg;

    public TicketServerException(String message, int code, String msg) {
        super(message);
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
