package com.time_em.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin on 11/23/2016.
 */

public class MutipleWorksSiteUser {

    @SerializedName("UserId")
    public String UserId;

    @SerializedName("mutiUserworkSiteList")
    public ArrayList<ListSites> mutiUserworkSiteList=new ArrayList<>();

}
