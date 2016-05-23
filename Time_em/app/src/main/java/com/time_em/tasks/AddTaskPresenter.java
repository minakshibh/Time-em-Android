package com.time_em.tasks;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTaskPresenter {

    private final IAddTaskView view;
    private final Activity act;

    private User user;
    private Time_emJsonParser parser;
    int userId;

    public AddTaskPresenter(IAddTaskView view, Activity act) {
        this.view = view;
        this.act = act;
    }

    public void Init(int userId) {
        this.userId = userId;
        user = new User();
        parser = new Time_emJsonParser(act);

        String timeStamp = Utils.getSharedPrefs(act, userId + act.getResources().getString(R.string.taskTimeStampStr));
        if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
            timeStamp = "";

        HashMap<String, String> postDataParameters = new HashMap<String, String>();
        postDataParameters.put("userId", String.valueOf(userId));
        Log.e("values", "userid: " + String.valueOf(userId));
        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                act, "get", Utils.getSpinnerTypeAPI,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) act;
        mWebPageTask.execute();
    }

    public void SpinnerApiData(ArrayList<TaskEntry> taskEntries) {

        ArrayList<String> SpinnerTaskList = new ArrayList<>();
        for (int i = 0; i < taskEntries.size(); i++) {
            SpinnerTaskList.add(taskEntries.get(i).getTaskName());
        }
        view.LoadSpinnerData(SpinnerTaskList ,taskEntries);
    }

    public void Init(int activityId, int userId, String numberOfHoursStr, String commentStr, int taskId, String taskName, String CreatedDate, String id) {

        String timeStamp = Utils.getSharedPrefs(act, userId + act.getResources().getString(R.string.taskTimeStampStr));
        if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
            timeStamp = "";

        HashMap<String, String> postDataParameters = new HashMap<String, String>();
        postDataParameters.put("UserId", String.valueOf(userId));
        postDataParameters.put("ActivityId", String.valueOf(activityId));
        postDataParameters.put("TimeSpent", numberOfHoursStr);
        postDataParameters.put("Comments", commentStr);
        postDataParameters.put("TaskId", String.valueOf(taskId));
        postDataParameters.put("TaskName", taskName);
        postDataParameters.put("CreatedDate", CreatedDate);
        postDataParameters.put("ID", id);

        Log.e("values", "userid: " + String.valueOf(userId));
        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                act, "post", Utils.GetAddUpdateUserTaskAPI,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) act;
        mWebPageTask.execute();
    }

    public interface IAddTaskView {
        public void LoadSpinnerData(ArrayList<String> taskEntries, ArrayList<TaskEntry> entries);
    }
}
