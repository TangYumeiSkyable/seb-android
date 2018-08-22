package com.broadlink;

/**
 * Created by wangkun on 16/06/2017.
 */

public class BLResponse {
    public static final int SUCCESS = 0;

    public int status = -1 ;
    public String msg;

    public boolean isSuccessful() {
        return status == SUCCESS;
    }
}
