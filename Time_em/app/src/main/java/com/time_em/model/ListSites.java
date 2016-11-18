package com.time_em.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin on 11/17/2016.
 */

public class ListSites {

    @SerializedName("SiteName")
    public String  SiteName;

    @SerializedName("WerksiteDates")
    public ArrayList<mutiUserworkSiteList> WerksiteDates=new ArrayList<>();



    public ArrayList<mutiUserworkSiteList> getWorkSiteDates() {
        return WerksiteDates;
    }

    public void setWorkSiteDates(ArrayList<mutiUserworkSiteList> WorkSiteDates) {
        this.WerksiteDates = WorkSiteDates;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String SiteName) {
        this.SiteName = SiteName;
    }



}
