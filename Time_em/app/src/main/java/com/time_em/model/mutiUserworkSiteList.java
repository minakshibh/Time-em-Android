package com.time_em.model;

import java.util.ArrayList;

/**
 * Created by admin on 7/25/2016.
 */

public class mutiUserworkSiteList {

    ArrayList<WorkSiteList> arraylist_WorkSiteList=new ArrayList<>();
    String date;

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
