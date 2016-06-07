package com.time_em.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.time_em.model.Notification;
import com.time_em.model.TaskEntry;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.tasks.AddTaskActivity;
import com.time_em.tasks.TaskDetailActivity;
import com.time_em.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NotificationListActivity extends Activity implements AsyncResponseTimeEm{

    private ListView notificationListView;
    private ArrayList<Notification> notifications;
    private Time_emJsonParser parser;
    private ImageView sendNotification, back;
    private TextView headerText;
    private String selectedNotificationType = "Notice";
    private TimeEmDbHandler dbHandler;
    private ImageView notices, messages, files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        initScreen();
        setUpClickListeners();
        getNotificationList();
    }

    private void initScreen() {
        sendNotification = (ImageView) findViewById(R.id.AddButton);
        sendNotification.setVisibility(View.GONE);
        back = (ImageView)findViewById(R.id.back);
        notificationListView = (ListView) findViewById(R.id.notificationList);
        parser = new Time_emJsonParser(NotificationListActivity.this);
        headerText = (TextView)findViewById(R.id.headerText);
        notices = (ImageView)findViewById(R.id.notices);
        messages = (ImageView) findViewById(R.id.messages);
        files = (ImageView)findViewById(R.id.files);

        notices.setBackgroundColor(getResources().getColor(R.color.gradientBgStart));
        messages.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));
        files.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));

        headerText.setText("Notifications");
        dbHandler = new TimeEmDbHandler(NotificationListActivity.this);
    }



    private void setUpClickListeners() {
        sendNotification.setOnClickListener(listener);
        back.setOnClickListener(listener);
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Intent intent = new Intent(NotificationListActivity.this, TaskDetailActivity.class);
//                intent.putExtra("taskEntry", tasks.get(position));
//                startActivity(intent);
            }
        });
        notices.setOnClickListener(listener);
        messages.setOnClickListener(listener);
        files.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == notices){
                notices.setBackgroundColor(getResources().getColor(R.color.gradientBgStart));
                messages.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));
                files.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));

                selectedNotificationType = "Notice";
                loadNotificationsByType();
            }else if(v == messages){
                notices.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));
                messages.setBackgroundColor(getResources().getColor(R.color.gradientBgStart));
                files.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));

                selectedNotificationType = "Message";
                loadNotificationsByType();
            }else if(v == files){
                notices.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));
                messages.setBackgroundColor(getResources().getColor(R.color.gradientBgEnd));
                files.setBackgroundColor(getResources().getColor(R.color.gradientBgStart));

                selectedNotificationType = "File";
                loadNotificationsByType();
            }else if(v == back){
                finish();
            }else if(v == sendNotification){
                Intent intent = new Intent(NotificationListActivity.this, SendNotification.class);
                startActivity(intent);
            }
        }
    };

    private void getNotificationList() {

        if (Utils.isNetworkAvailable(NotificationListActivity.this)) {

            String timeStamp = Utils.getSharedPrefs(NotificationListActivity.this, HomeActivity.user.getId() + getResources().getString(R.string.notificationTimeStampStr));
            if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
                timeStamp = "";

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("UserId", String.valueOf(HomeActivity.user.getId()));
            postDataParameters.put("timeStamp", timeStamp);

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    NotificationListActivity.this, "post", Utils.GetNotificationAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) NotificationListActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(NotificationListActivity.this, Utils.network_error);
        }
    }

   /* private void deleteTask(int taskEntryId) {

        if (Utils.isNetworkAvailable(NotificationListActivity.this)) {
            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("Id", String.valueOf(taskEntryId));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    NotificationListActivity.this, "post", Utils.deleteTaskAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) NotificationListActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(NotificationListActivity.this, Utils.network_error);
        }
    }*/

    public class NotificationAdapter extends BaseSwipeAdapter {
        private TextView subject, message, senderName;
        private LinearLayout delete;

        public NotificationAdapter() {
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return notifications.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public void fillValues(final int position, View convertView) {
            // TODO Auto-generated method stub

            final Notification notification = notifications.get(position);

            delete = (LinearLayout) convertView.findViewById(R.id.delete);
            subject = (TextView) convertView.findViewById(R.id.subject);
            message = (TextView) convertView.findViewById(R.id.message);
            senderName = (TextView) convertView.findViewById(R.id.senderName);

            subject.setText(notification.getSubject());
            message.setText(notification.getMessage());
            senderName.setText(notification.getSenderFullName());

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            NotificationListActivity.this);
                    alert.setTitle("Local copy of this notification will be deleted.");
                    alert.setMessage("Are you sure?");
                    alert.setPositiveButton("No", null);
                    alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHandler.deleteNotification(notifications.get(position).getNotificationId());
                            notifications.remove(position);
                            notifyDataSetChanged();
                        }
                    });

                    alert.show();
                }
            });
        }

        @Override
        public View generateView(int arg0, ViewGroup arg1) {
            return LayoutInflater.from(NotificationListActivity.this).inflate(
                    R.layout.notification_row, null);

        }

        @Override
        public int getSwipeLayoutResourceId(int arg0) {
            // TODO Auto-generated method stub
            return R.id.not_swipe;
        }
    }

    private void loadNotificationsByType(){
        notifications.clear();
        notifications = dbHandler.getNotificationsByType(selectedNotificationType);
        notificationListView.setAdapter(new NotificationAdapter());
    }

    @Override
    public void processFinish(String output, String methodName) {
        // TODO Auto-generated method stub
        Log.e("output", ":: " + output);
        Utils.alertMessage(NotificationListActivity.this, output);
        notifications = parser.parseNotificationList(output);
        dbHandler.updateNotifications(notifications);

        loadNotificationsByType();

        /*if(methodName.equals(Utils.getTaskListAPI)) {
            ArrayList<TaskEntry> taskEntries = parser.parseTaskList(output, UserId, selectedDate);
            TimeEmDbHandler dbHandler = new TimeEmDbHandler(NotificationListActivity.this);
            dbHandler.updateTask(taskEntries, selectedDate);

            tasks = dbHandler.getTaskEnteries(UserId, selectedDate);
            taskListview.setAdapter(new TaskAdapter(NotificationListActivity.this));
        }else if(methodName.equals(Utils.deleteTaskAPI)) {
            boolean error = parser.parseDeleteTaskResponse(output);
            if(!error) {
                getTaskList(selectedDate);
            }
        }*/
    }


}