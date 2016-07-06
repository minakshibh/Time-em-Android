package com.time_em.tasks;


public class AddTaskPresenter {

   /* private final IAddTaskView view;
    private final Activity act;

    private User user;
    private Time_emJsonParser parser;
    private TaskEntryPre taskEntryPre;
    int userId;

    public AddTaskPresenter(IAddTaskView view, Activity act) {
        this.view = view;
        this.act = act;
    }

    public void Init(int userId, TaskEntry taskEntry) {
        this.userId = userId;
        user = new User();
        parser = new Time_emJsonParser(act);
        taskEntryPre = new TaskEntryPre();

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

        if (taskEntry != null) {
            taskEntryPre.CreatedDate = taskEntry.getCreatedDate();
            taskEntryPre.CommentStr = taskEntry.getComments();
            taskEntryPre.TimeSpent = taskEntry.getTimeSpent();
            taskEntryPre.TaskName = taskEntry.getTaskName();

            view.LoadCreateDate(taskEntryPre.CreatedDate);

            if (taskEntryPre.CommentStr != null)
                view.LoadComment(taskEntryPre.CommentStr);

            if (taskEntryPre.TimeSpent != null)
                view.LoadTimeSpent(taskEntryPre.TimeSpent);

            if (taskEntryPre.TaskName != null)
                view.LoadTaskName(taskEntryPre.TaskName);
        }

    }

    public void SpinnerApiData(ArrayList<TaskEntry> taskEntries) {

        ArrayList<String> SpinnerTaskList = new ArrayList<>();
        for (int i = 0; i < taskEntries.size(); i++) {
            SpinnerTaskList.add(taskEntries.get(i).getTaskName());
        }
        view.LoadSpinnerData(SpinnerTaskList, taskEntries);
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

        void LoadCreateDate(String createdDate);

        void LoadComment(String commentStr);

        void LoadTimeSpent(Double signinHours);

        void LoadTaskName(String taskName);
    }

    private class TaskEntryPre {
        public String CreatedDate;
        public String TaskName;
        public String CommentStr;
        public Double TimeSpent;

    }*/
}
