package com.supor.suporairclear.config;

/**
 * Created by fengjian on 2017/11/21.
 * function:
 */

public class SubDominUtil {
    public static String findNameById(long subDomainId) {
        if (subDomainId == SubDominConfig.XL.mSubDominId) {
            return SubDominConfig.XL.mSubDominName;
        } else if (subDomainId == SubDominConfig.XS.mSubDominId) {
            return SubDominConfig.XS.mSubDominName;
        } else if (subDomainId == SubDominConfig.XL_US.mSubDominId) {
            return SubDominConfig.XL_US.mSubDominName;
        } else if (subDomainId == SubDominConfig.XS_US.mSubDominId) {
            return SubDominConfig.XS_US.mSubDominName;
        } else if (subDomainId == SubDominConfig.TEFAL_XL.mSubDominId) {
            return SubDominConfig.TEFAL_XL.mSubDominName;
        } else if (subDomainId == SubDominConfig.TEFAL_XS.mSubDominId) {
            return SubDominConfig.TEFAL_XS.mSubDominName;
        } else {
            return null;
        }
    }
}
