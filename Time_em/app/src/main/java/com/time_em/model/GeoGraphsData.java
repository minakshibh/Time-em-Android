package com.time_em.model;


public class GeoGraphsData {
    String date,GeoId,  name,hours,UserId;

    public String getDate() {
        return date;
    }

    public String getGeoId() {
        return GeoId;
    }

    public void setGeoId(String geoId) {
        GeoId = geoId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
}
