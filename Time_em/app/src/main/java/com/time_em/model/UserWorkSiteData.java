package com.time_em.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin on 11/17/2016.
 */

public class UserWorkSiteData {

    @SerializedName("ListSites")
    public ArrayList<ListSites> ListSites = new ArrayList<>();

    public ArrayList<ListSites> getListSite() {
        return ListSites;
    }

    public void setListSite(ArrayList<ListSites> listSite) {
        ListSites = listSite;
    }

}
