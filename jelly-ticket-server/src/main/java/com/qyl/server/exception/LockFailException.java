package com.qyl.server.exception;

/**
 * @Author: qyl
 * @Date: 2021/4/10 14:45
 */
public class LockFailException extends RuntimeException {

    public LockFailException(String message) {
        super(message);
    }
}
