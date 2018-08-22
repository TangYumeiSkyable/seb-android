package com.supor.suporairclear.model;

/**
 * Created by enyva on 16/6/16.
 */
public class CommendInfo {
    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public CommendInfo() {
    }

    private Long deviceId;
    private String commend;

    public CommendInfo(Long deviceId, String commend, Integer value) {
        this.deviceId = deviceId;
        this.commend = commend;
        this.value = value;
    }

    private Integer value;
}
