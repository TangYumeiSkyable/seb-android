package com.broadlink.networkapi;

/**
 * Created by wangkun on 16/06/2017.
 */

public class ScanRequest {
    public int scantime = 3000;
    public int scaninterval = 950;

    public ScanRequest(int scantime) {
        this.scantime = scantime;
    }
}
