package com.supor.suporairclear.model;

/**
 * Created by enyva on 16/6/14.
 */
public class WorkMode {
    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    private String workStatus;
    private String weekDay;

    public WorkMode(String workStatus, String weekDay, String startTime, String endTime, String selected, String unselected) {
        this.workStatus = workStatus;
        this.weekDay = weekDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.selected = selected;
        this.unselected = unselected;
    }

    public WorkMode() {
    }

    private String startTime;
    private String endTime;
    private String selected;
    private String unselected;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getUnselected() {
        return unselected;
    }

    public void setUnselected(String unselected) {
        this.unselected = unselected;
    }
}
