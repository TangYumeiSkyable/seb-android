package com.supor.suporairclear.model;

import java.io.Serializable;

/**
 * Created by emma on 2017/5/18.
 */

public class Replacementremind implements Serializable{


    private String filtername1;

    private String filtername2;

    private String filtername3;
    private String filtername4;

    public String getFiltername4() {
        return filtername4;
    }

    public void setFiltername4(String filtername4) {
        this.filtername4 = filtername4;
    }

    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFiltername3() {
        return filtername3;
    }

    public void setFiltername3(String filtername3) {
        this.filtername3 = filtername3;
    }



    public String getFiltername2() {
        return filtername2;
    }

    public void setFiltername2(String filtername2) {
        this.filtername2 = filtername2;
    }



    public String getFiltername1() {
        return filtername1;
    }

    public void setFiltername1(String filtername) {
        this.filtername1 = filtername;
    }




}
