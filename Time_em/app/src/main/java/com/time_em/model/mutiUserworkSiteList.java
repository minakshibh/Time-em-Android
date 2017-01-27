package com.time_em.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin on 7/25/2016.
 */

public class mutiUserworkSiteList {

    @SerializedName("workSiteList")
    public ArrayList<WorkSiteList> workSiteList=new ArrayList<>();

    @SerializedName("CreatedDate")
    public String CreatedDate;



    public ArrayList<WorkSiteList> getArraylist_WorkSiteList() {
        return workSiteList;
    }

    public void setArraylist_WorkSiteList(ArrayList<WorkSiteList> arraylist_WorkSiteList) {
        this.workSiteList = arraylist_WorkSiteList;
    }

    public String getDate() {
        return CreatedDate;
    }

    public void setDate(String date) {
        this.CreatedDate = date;
    }

}
