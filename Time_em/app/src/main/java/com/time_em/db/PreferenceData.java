package com.time_em.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.time_em.db.Interface.IPreferenceData;

public class PreferenceData implements IPreferenceData {

    private static final String USER_ID = "userid";
    private static final String ACTIVITY_ID = "activityid";


    SharedPreferences prefs;
    Context context;

    public PreferenceData(Context ctx) {
        this.context = ctx;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void ClearPrefs() {
        //hold on to a few things
        String ActivityId = GetActivityId();
        String UserId = GetUserId();

        prefs.edit().clear().commit();

        SetActivityId(ActivityId);
        SetUserId(UserId);
    }

    @Override
    public String GetUserId() {
        return prefs.getString(USER_ID, "");
    }

    @Override
    public void SetUserId(String UserId) {
        prefs.edit().putString(USER_ID, UserId).commit();
    }

    @Override
    public String GetActivityId() {
        return prefs.getString(ACTIVITY_ID, "");
    }

    @Override
    public void SetActivityId(String ActivityId) {
        prefs.edit().putString(ACTIVITY_ID, ActivityId).commit();
    }
}
