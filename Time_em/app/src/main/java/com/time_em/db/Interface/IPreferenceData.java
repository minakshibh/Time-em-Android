package com.time_em.db.Interface;


public interface IPreferenceData {
    void ClearPrefs();

    String GetUserId();
    void SetUserId(String UserId);

    String GetActivityId();
    void SetActivityId(String ActivityId);
}
