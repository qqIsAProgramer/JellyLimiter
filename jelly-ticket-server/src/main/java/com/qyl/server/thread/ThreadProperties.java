package com.qyl.server.thread;

import org.springframework.stereotype.Component;

/**
 * @Author: qyl
 * @Date: 2021/3/27 11:21
 */
@Component
public class ThreadProperties {

    private int size = Runtime.getRuntime().availableProcessors();  // CPU 核数

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
