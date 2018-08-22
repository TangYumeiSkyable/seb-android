package com.supor.suporairclear.model;

/**
 * Created by Administrator on 2015/4/25.
 */
public class UserInfo {
    private long userId;
    private String nickName;
    private String phone;
    public UserInfo(long userId, String nickName, String phone) {
        this.userId = userId;
        this.nickName = nickName;
        this.phone = phone;
    }
    public UserInfo(long userId) {
        this.userId = userId;
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
