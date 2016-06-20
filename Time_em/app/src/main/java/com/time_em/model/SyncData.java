package com.time_em.model;

import java.util.ArrayList;

/**
 * Created by krishnauser2 on 6/20/2016.
 */
public class SyncData {
    ArrayList<TaskEntry> array_taks=new ArrayList<>();
    ArrayList<Notification> array_noitification=new ArrayList<>();

    public ArrayList<TaskEntry> getArray_taks() {
        return array_taks;
    }

    public void setArray_taks(ArrayList<TaskEntry> array_taks) {
        this.array_taks = array_taks;
    }

    public ArrayList<Notification> getArray_noitification() {
        return array_noitification;
    }

    public void setArray_noitification(ArrayList<Notification> array_noitification) {
        this.array_noitification = array_noitification;
    }
}
