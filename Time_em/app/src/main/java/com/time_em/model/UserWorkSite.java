package com.time_em.model;


import java.util.ArrayList;

public class UserWorkSite {
    String date;
    ArrayList<WorkSiteList> arraylist_WorkSiteList=new ArrayList<>();

    public ArrayList<WorkSiteList> getArraylist_WorkSiteList() {
        return arraylist_WorkSiteList;
    }

    public void setArraylist_WorkSiteList(ArrayList<WorkSiteList> arraylist_WorkSiteList) {
        this.arraylist_WorkSiteList = arraylist_WorkSiteList;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


}
