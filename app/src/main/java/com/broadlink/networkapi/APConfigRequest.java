package com.broadlink.networkapi;

/**
 * Created by wangkun on 16/06/2017.
 */

public class APConfigRequest {
    public static final int ENCRYPT_NONE = 0;
    public static final int ENCRYPT_WEP = 1;
    public static final int ENCRYPT_WPA = 2;
    public static final int ENCRYPT_WPA2 = 3;
    public static final int ENCRYPT_WPA_BOTH = 4;

    public final String ssid;
    public final String password;
    public final int type;

    public APConfigRequest(String ssid, String password) {
        this(ssid, password, password.length() > 0 ? APConfigRequest.ENCRYPT_WPA_BOTH : APConfigRequest.ENCRYPT_NONE);
    }

    public APConfigRequest(String ssid, String password, int type) {
        this.ssid = ssid;
        this.password = password;
        this.type = type;
    }
}
