package com.time_em.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.TaskEntry;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

public class TaskListActivity extends Activity implements AsyncResponseTimeEm {

    private ListView taskListview;
    private ArrayList<TaskEntry> tasks;
    private Time_emJsonParser parser;
    private int UserId;
    private ImageView addTaskButton, back;
    private TextView headerText;
    private Intent intent;
    private LinearLayout footer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        initScreen();

    }

    private void initScreen() {
        addTaskButton = (ImageView) findViewById(R.id.AddButton);
        addTaskButton.setVisibility(View.GONE);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        taskListview = (ListView) findViewById(R.id.taskList);
        parser = new Time_emJsonParser(TaskListActivity.this);

        taskListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskEntry", tasks.get(position));
                startActivity(intent);
            }
        });
        headerText = (TextView)findViewById(R.id.headerText);
        SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yyyy");

        String currentDate = postFormater.format(new Date());
        UserId = getIntent().getIntExtra("UserId",HomeActivity.user.getId());

        if(UserId == HomeActivity.user.getId()){
            headerText.setText("My Tasks");
        }else{
            String username = getIntent().getStringExtra("UserName");
            headerText.setText(username+"'s Tasks");
        }
        footer = (LinearLayout)findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
        getTaskList("05-16-2016");
    }

    private void getTaskList(String createdDate) {

        if (Utils.isNetworkAvailable(TaskListActivity.this)) {

            String timeStamp = Utils.getSharedPrefs(TaskListActivity.this, UserId + getResources().getString(R.string.taskTimeStampStr));
            if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
                timeStamp = "";

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("userId", String.valueOf(UserId));
            postDataParameters.put("createdDate", createdDate);
            postDataParameters.put("TimeStamp", timeStamp);

            Log.e("values", "userid: " + String.valueOf(UserId) + ", createdDate: " + createdDate + ", TimeStamp: " + timeStamp);

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    TaskListActivity.this, "post", Utils.getTaskListAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(TaskListActivity.this, Utils.network_error);
        }
    }

    private void deleteTask(int taskEntryId) {

        if (Utils.isNetworkAvailable(TaskListActivity.this)) {
            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("Id", String.valueOf(taskEntryId));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    TaskListActivity.this, "post", Utils.deleteTaskAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(TaskListActivity.this, Utils.network_error);
        }
    }

    public class TaskAdapter extends BaseSwipeAdapter {
        private Context context;
        private TextView taskName, hours, taskComments;
        private LinearLayout edit, delete;

        public TaskAdapter(Context ctx) {
            context = ctx;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tasks.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return tasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public void fillValues(final int position, View convertView) {
            // TODO Auto-generated method stub

            final TaskEntry selectedtask = tasks.get(position);

            taskName = (TextView) convertView.findViewById(R.id.taskName);
            hours = (TextView) convertView.findViewById(R.id.hours);
            delete = (LinearLayout) convertView.findViewById(R.id.delete);
            edit = (LinearLayout) convertView.findViewById(R.id.edit);
            taskComments = (TextView) convertView.findViewById(R.id.taskComments);

            taskName.setText(selectedtask.getTaskName());
            taskComments.setText(selectedtask.getComments());
            hours.setText("(" + String.valueOf(selectedtask.getSignedInHours()) + ") Hours");

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            TaskListActivity.this);
                    alert.setTitle("Delete this task?");
                    alert.setMessage("Are you sure?");
                    alert.setPositiveButton("No", null);
                    alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask(tasks.get(position).getId());
                        }
                    });

                    alert.show();
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public View generateView(int arg0, ViewGroup arg1) {
            return LayoutInflater.from(TaskListActivity.this).inflate(
                    R.layout.task_row, null);

        }

        @Override
        public int getSwipeLayoutResourceId(int arg0) {
            // TODO Auto-generated method stub
            return R.id.swipe;
        }
    }

    @Override
    public void processFinish(String output, String methodName) {
        // TODO Auto-generated method stub
        Log.e("output", ":: " + output);
        if(methodName.equals(Utils.getTaskListAPI)) {
            ArrayList<TaskEntry> taskEntries = parser.parseTaskList(output, UserId);
            TimeEmDbHandler dbHandler = new TimeEmDbHandler(TaskListActivity.this);
            dbHandler.updateTask(taskEntries);

            tasks = dbHandler.getTaskEnteries(UserId);
            taskListview.setAdapter(new TaskAdapter(TaskListActivity.this));
        }else if(methodName.equals(Utils.deleteTaskAPI)) {
//            Utils.alertMessage(TaskListActivity.this, output);
            boolean error = parser.parseDeleteTaskResponse(output);
            if(!error)
                getTaskList("05-16-2016");
        }
    }
}
