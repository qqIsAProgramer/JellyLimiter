package com.qyl.monitor.entity;

import com.qyl.common.utils.DateTimeUtil;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: qyl
 * @Date: 2021/3/28 22:09
 */
@Data
public class Monitor implements Comparable<Monitor> {
    /**
     * 与 LimiterRule 对应
     */
    private String app;

    private String id;

    private String name;

    /**
     * 监控时长（秒）
     */
    private long monitorTime;

    /**
     * 与监控日期相关
     */
    private String dateTime;

    private LocalDateTime localDateTime;

    public Monitor() {
    }

    public Monitor(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getDateTime() {
        return DateTimeUtil.toString(localDateTime);
    }

    @Override
    public int compareTo(Monitor o) {
        return this.getLocalDateTime().compareTo(o.getLocalDateTime());
    }
}
