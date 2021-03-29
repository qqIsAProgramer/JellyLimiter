package com.qyl.server.exception;

import com.qyl.server.utils.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author: qyl
 * @Date: 2021/3/25 19:14
 */
@RestControllerAdvice
public class ExceptionAdvice {

    // 日志记录
    private static Logger logger = LoggerFactory.getLogger(TicketServerException.class);

    /**
     * 处理 TicketServerException
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Void> ticketServerExceptionHandler(Exception e) {
        if (e instanceof TicketServerException) {
            TicketServerException ticketServerException = (TicketServerException) e;
            return ResponseResult.fail(ticketServerException.getCode(), ticketServerException.getMessage());
        } else {
            logger.error("[系统异常] {}", e.getMessage());
            return ResponseResult.fail();
        }
    }
}
