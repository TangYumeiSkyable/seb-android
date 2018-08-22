package com.supor.suporairclear.model;

/**
 * Created by enyva on 16/6/2.
 */
public class DeviceInfo {
    long deviceId;
    String deviceName;
    long subDomainId;

    public String getSubDomainName() {
        return subDomainName;
    }

    public void setSubDomainName(String subDomainName) {
        this.subDomainName = subDomainName;
    }

    String subDomainName;
    long status;
    String physicalDeviceId;
    long owner;

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", subDomainId=" + subDomainId +
                ", status=" + status +
                ", physicalDeviceId='" + physicalDeviceId + '\'' +
                ", owner=" + owner +
                '}';
    }

    public DeviceInfo() {
    }

    public DeviceInfo(long deviceId, String deviceName, long subDomainId, String subDomainName,long status, String physicalDeviceId, long owner) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.subDomainId = subDomainId;
        this.status = status;
        this.physicalDeviceId = physicalDeviceId;
        this.owner = owner;
        this.subDomainName = subDomainName;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getSubDomainId() {
        return subDomainId;
    }

    public void setSubDomainId(long subDomainId) {
        this.subDomainId = subDomainId;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getPhysicalDeviceId() {
        return physicalDeviceId;
    }

    public void setPhysicalDeviceId(String physicalDeviceId) {
        this.physicalDeviceId = physicalDeviceId;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }
}
