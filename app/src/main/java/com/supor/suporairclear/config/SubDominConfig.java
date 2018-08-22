package com.supor.suporairclear.config;

/**
 * Created by fengjian on 2017/11/21.L
 * function:
 */

public enum SubDominConfig {

    XL("rowentaxl", 5376L),
    XS("rowentaxs", 5561L),
    XL_US("rowentaxlus", 6495L),
    XS_US("rowentaxsus", 6496L),
    TEFAL_XL("tefalxl", 6497L),
    TEFAL_XS("tefalxs", 6498L);


    public String mSubDominName;
    public Long mSubDominId;

    SubDominConfig(String subDominName, long subdominId) {
        this.mSubDominName = subDominName;
        this.mSubDominId = subdominId;
    }
}





