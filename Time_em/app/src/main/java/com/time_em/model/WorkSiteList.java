package com.time_em.model;


public class WorkSiteList {
   String WorkSiteId, WorkSiteName,WorkingHour,TimeOut,TimeIn;

    public String getTimeOut() {
        return TimeOut;
    }

    public void setTimeOut(String timeOut) {
        TimeOut = timeOut;
    }

    public String getTimeIn() {
        return TimeIn;
    }

    public void setTimeIn(String timeIn) {
        TimeIn = timeIn;
    }

    public String getWorkSiteId() {
        return WorkSiteId;
    }

    public void setWorkSiteId(String workSiteId) {
        WorkSiteId = workSiteId;
    }

    public String getWorkSiteName() {
        return WorkSiteName;
    }

    public void setWorkSiteName(String workSiteName) {
        WorkSiteName = workSiteName;
    }

    public String getWorkingHour() {
        return WorkingHour;
    }

    public void setWorkingHour(String workingHour) {
        WorkingHour = workingHour;
    }
}
