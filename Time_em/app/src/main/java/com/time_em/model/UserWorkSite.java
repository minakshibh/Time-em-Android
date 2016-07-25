package com.time_em.model;

import java.util.ArrayList;

public class UserWorkSite {
    String date,allData,userid;
    ArrayList<WorkSiteList> arraylist_WorkSiteList=new ArrayList<>();
    ArrayList<mutiUserworkSiteList> arraylist_multiUserWorkSiteList=new ArrayList<>();

    public ArrayList<WorkSiteList> getArraylist_WorkSiteList() {
        return arraylist_WorkSiteList;
    }

    public void setArraylist_WorkSiteList(ArrayList<WorkSiteList> arraylist_WorkSiteList) {
        this.arraylist_WorkSiteList = arraylist_WorkSiteList;
    }

    public ArrayList<mutiUserworkSiteList> getArraylist_multiUserWorkSiteList() {
        return arraylist_multiUserWorkSiteList;
    }

    public void setArraylist_multiUserWorkSiteList(ArrayList<mutiUserworkSiteList> arraylist_WorkSiteList) {
        this.arraylist_multiUserWorkSiteList = arraylist_WorkSiteList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAllData() {
        return allData;
    }

    public void setAllData(String allData) {
        this.allData = allData;
    }

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userid) {
        this.userid = userid;
    }

}
