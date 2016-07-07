package com.time_em.notifications;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.inappbilling.PurchaseActivity;
import com.time_em.model.Notification;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NotificationListActivity extends Activity implements AsyncResponseTimeEm{

    private ListView notificationListView;
    private ArrayList<Notification> notifications=new ArrayList<>();
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
       // getNotificationList();
    }

    private void initScreen() {
        sendNotification = (ImageView) findViewById(R.id.AddButton);
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
        notices.setOnClickListener(listener);
        messages.setOnClickListener(listener);
        files.setOnClickListener(listener);
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotificationListActivity.this, NotificationDetailsActivity.class);
                intent.putExtra("notification", notifications.get(position));
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(NotificationListActivity.this, PurchaseActivity.class);
                startActivity(intent);
                //showPurchaseDialog();

            }
        }
    };

   /* public void showPurchaseDialog(){

        final PurchaseItem pi=new PurchaseItem();
        pi.setup(getApplicationContext());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotificationListActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Want to send Notifications ?");
        // Setting Dialog Message
        alertDialog.setMessage("Buy notification access in just $50. ");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.icon);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                dialog.cancel();
                pi.buyClick(NotificationListActivity.this);
                //pi.consumeItem();

            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }*/

    private void getNotificationList() {

       // if (Utils.isNetworkAvailable(NotificationListActivity.this)) {

            String timeStamp = Utils.getSharedPrefs(NotificationListActivity.this, HomeActivity.user.getId() + getResources().getString(R.string.notificationTimeStampStr));
            if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
                timeStamp = "";

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("UserId", String.valueOf(HomeActivity.user.getId()));
            postDataParameters.put("timeStamp", timeStamp);

            Log.e("ss","ss"+postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    NotificationListActivity.this, "post", Utils.GetNotificationAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) NotificationListActivity.this;
            mWebPageTask.execute();

        //} else {
          //  Utils.alertMessage(NotificationListActivity.this, Utils.network_error);
       // }
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
        private TextView subject, message, senderName,date;
        private LinearLayout delete;
        private String part1,part2;
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
            date = (TextView) convertView.findViewById(R.id.date);

            subject.setText(notification.getSubject());
            message.setText(notification.getMessage());
            senderName.setText(notification.getSenderFullName());
          //  date.setText(notification.getSenderFullName());
            String getDate=notification.getCreatedDate();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();

            //System.out.println(dateFormat.format(cal.getTime()));
            String getCurrentDate=dateFormat.format(cal.getTime());
            try {
                String[] parts = getDate.split(" ");
                part1 = parts[0]; // date
                part2 = parts[1]; //time

                System.out.println(part1);
                System.out.println(part2);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            if (getCurrentDate.equalsIgnoreCase(part1)) {

                date.setText(part2);//time
            }else{

               //date.setText(part1);//date
               String str_date= formatDateFromOnetoAnother(part1,"dd/MM/yyyy","EEE dd MMM, yyyy");
               date.setText(str_date);

            }


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
        notifications = dbHandler.getNotificationsByType(selectedNotificationType,false,"false");
        notificationListView.setAdapter(new NotificationAdapter());
    }

    @Override
    public void processFinish(String output, String methodName) {
        // TODO Auto-generated method stub
        Log.e("output", ":: " + output);
     //   Utils.alertMessage(NotificationListActivity.this, output);
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
    public static String formatDateFromOnetoAnother(String date,String givenformat,String resultformat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(sdf.parse(date));
        }
        catch(Exception e) {
            e.printStackTrace();
            return "";
        }
        finally {
            sdf=null;
            sdf1=null;
        }
        return result;
    }
    @Override
    protected void onResume() {
        super.onResume();
        getNotificationList();
    }
}